import { type SyntheticEvent, useEffect, useMemo, useState } from "react";
import "chart.js/auto";
import { Button } from "primereact/button";
import { Card } from "primereact/card";
import { Chart } from "primereact/chart";
import { Column } from "primereact/column";
import { DataTable } from "primereact/datatable";
import { InputText } from "primereact/inputtext";
import { Message } from "primereact/message";
import { Skeleton } from "primereact/skeleton";
import { Tag } from "primereact/tag";
import { scoresApi } from "../../api/scores";
import PageMeta from "../../components/common/PageMeta";
import type { Language } from "../../context/LanguageContext";
import { useLanguage } from "../../context/LanguageContext";
import { useTheme } from "../../context/ThemeContext";
import type {
  ScoreLevelReportResponse,
  ScoreResponse,
  SubjectLevelStatsResponse,
  TopGroupAStudentResponse,
} from "../../types/scores";

const levelColors = {
  excellent: "#0f8f77",
  good: "#2f80ed",
  average: "#f59e0b",
  belowAverage: "#ef476f",
};

export default function Home() {
  const { language, t } = useLanguage();
  const { theme } = useTheme();
  const [registrationNumber, setRegistrationNumber] = useState("01000001");
  const [score, setScore] = useState<ScoreResponse | null>(null);
  const [report, setReport] = useState<ScoreLevelReportResponse | null>(null);
  const [topGroupA, setTopGroupA] = useState<TopGroupAStudentResponse[]>([]);
  const [dashboardLoading, setDashboardLoading] = useState(true);
  const [scoreLoading, setScoreLoading] = useState(false);
  const [dashboardError, setDashboardError] = useState<string | null>(null);
  const [scoreError, setScoreError] = useState<string | null>(null);
  const normalizedRegistrationNumber = registrationNumber.trim();
  const canSearch = /^\d{8}$/.test(normalizedRegistrationNumber);
  const groupALabel = `${t("math")} + ${t("physics")} + ${t("chemistry")}`;

  useEffect(() => {
    let ignored = false;
    setDashboardLoading(true);
    Promise.all([scoresApi.getScoreLevels(), scoresApi.getTopGroupA()])
      .then(([scoreLevels, topStudents]) => {
        if (!ignored) {
          setReport(scoreLevels);
          setTopGroupA(topStudents);
          setDashboardError(null);
        }
      })
      .catch((error: Error) => {
        if (!ignored) {
          setDashboardError(getErrorMessage(error, t("loadFailed")));
        }
      })
      .finally(() => {
        if (!ignored) {
          setDashboardLoading(false);
        }
      });

    return () => {
      ignored = true;
    };
  }, [t]);

  const chartData = useMemo(() => {
    const subjects = report?.subjects ?? [];
    return {
      labels: subjects.map((subject) => getSubjectName(subject, language)),
      datasets: [
        {
          label: t("excellent"),
          data: subjects.map((subject) => subject.excellent),
          backgroundColor: levelColors.excellent,
          borderRadius: 6,
        },
        {
          label: t("good"),
          data: subjects.map((subject) => subject.good),
          backgroundColor: levelColors.good,
          borderRadius: 6,
        },
        {
          label: t("average"),
          data: subjects.map((subject) => subject.average),
          backgroundColor: levelColors.average,
          borderRadius: 6,
        },
        {
          label: t("belowAverage"),
          data: subjects.map((subject) => subject.belowAverage),
          backgroundColor: levelColors.belowAverage,
          borderRadius: 6,
        },
      ],
    };
  }, [language, report, t]);

  const chartOptions = useMemo(() => {
    const textColor = theme === "dark" ? "#d6f5ea" : "#334155";
    const gridColor =
      theme === "dark" ? "rgba(214, 245, 234, 0.12)" : "rgba(15, 23, 42, 0.08)";

    return {
      maintainAspectRatio: false,
      responsive: true,
      plugins: {
        legend: {
          labels: {
            color: textColor,
            usePointStyle: true,
            boxWidth: 8,
          },
        },
      },
      scales: {
        x: {
          stacked: true,
          ticks: {
            color: textColor,
          },
          grid: {
            display: false,
          },
        },
        y: {
          stacked: true,
          ticks: {
            color: textColor,
          },
          grid: {
            color: gridColor,
          },
        },
      },
    };
  }, [theme]);

  const handleScoreSearch = (event: SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!canSearch) {
      setScoreError(t("invalidRegistrationNumber"));
      return;
    }

    setScoreLoading(true);
    setScoreError(null);
    scoresApi
      .getScore(normalizedRegistrationNumber)
      .then((candidateScore) => {
        setScore(candidateScore);
      })
      .catch((error: Error) => {
        setScore(null);
        setScoreError(getErrorMessage(error, t("loadFailed")));
      })
      .finally(() => setScoreLoading(false));
  };

  return (
    <>
      <PageMeta
        title="G-Scores | Golden Owl Assignment"
        description="Vietnam national exam score lookup and reports"
      />
      <div className="space-y-5">
        <section className="gscore-hero">
          <div className="min-w-0">
            <span className="gscore-kicker">{t("appSubtitle")}</span>
            <h1 className="mt-2 text-2xl font-bold text-slate-950 dark:text-white md:text-3xl">
              {t("dataWorkspace")}
            </h1>
            <p className="mt-2 max-w-3xl text-sm text-slate-600 dark:text-emerald-100/70 md:text-base">
              {t("dataWorkspaceDesc")}
            </p>
          </div>
          <div className="gscore-hero-mark">
            <i className="pi pi-chart-line" />
          </div>
        </section>

        {dashboardError && <Message severity="error" text={dashboardError} />}

        <section className="grid grid-cols-1 gap-4 md:grid-cols-3">
          <MetricCard
            icon="pi-database"
            label={t("importedRecords")}
            value={formatNumber(report?.importedRecords)}
            loading={dashboardLoading}
            tone="emerald"
          />
          <MetricCard
            icon="pi-book"
            label={t("subjects")}
            value={report?.subjects.length.toString() ?? "--"}
            loading={dashboardLoading}
            tone="blue"
          />
          <MetricCard
            icon="pi-trophy"
            label={t("completeGroupA")}
            value={topGroupA.length.toString()}
            loading={dashboardLoading}
            tone="coral"
          />
        </section>

        <section className="grid grid-cols-1 gap-5 xl:grid-cols-[minmax(320px,420px)_1fr]">
          <Card className="gscore-card">
            <div className="mb-5 flex items-start justify-between gap-4">
              <div>
                <span className="gscore-kicker">{t("scoreLookup")}</span>
                <h2 className="mt-2 text-lg font-bold text-slate-950 dark:text-white">
                  {t("registrationNumber")}
                </h2>
              </div>
              <Tag value="API" severity="success" rounded />
            </div>

            <form onSubmit={handleScoreSearch} className="gscore-search-form mb-5">
              <InputText
                value={registrationNumber}
                onChange={(event) =>
                  setRegistrationNumber(event.target.value.replace(/\D/g, "").slice(0, 8))
                }
                placeholder={t("searchPlaceholder")}
                inputMode="numeric"
                maxLength={8}
                aria-invalid={!canSearch}
                className="w-full min-w-0"
              />
              <Button
                type="submit"
                icon="pi pi-search"
                label={t("search")}
                loading={scoreLoading}
                disabled={!canSearch || scoreLoading}
                className="gscore-search-button"
              />
            </form>

            {scoreError && <Message severity="warn" text={scoreError} />}

            {!score && !scoreLoading && !scoreError && (
              <div className="gscore-empty">{t("noScoreYet")}</div>
            )}

            {scoreLoading && <ScoreSkeleton />}

            {score && !scoreLoading && (
              <div className="space-y-4">
                <div className="rounded-lg border border-emerald-900/10 bg-emerald-50 p-4 dark:border-emerald-300/10 dark:bg-white/[0.04]">
                  <div className="text-xs font-semibold uppercase text-slate-500 dark:text-emerald-100/60">
                    {t("student")}
                  </div>
                  <div className="mt-1 text-2xl font-black text-slate-950 dark:text-white">
                    {score.registrationNumber}
                  </div>
                  <div className="mt-3 grid grid-cols-2 gap-3 text-sm">
                    <ScoreMeta
                      label={t("foreignLanguageCode")}
                      value={score.foreignLanguageCode ?? "--"}
                    />
                    <ScoreMeta
                      label={t("groupATotal")}
                      value={formatScore(score.groupATotal)}
                    />
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-2 sm:grid-cols-3">
                  {score.scores.map((subject) => (
                    <div key={subject.code} className="gscore-score-pill">
                      <span>{language === "vi" ? subject.nameVi : subject.nameEn}</span>
                      <strong>{formatScore(subject.score)}</strong>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </Card>

          <Card id="reports" className="gscore-card">
            <div className="mb-4 flex flex-col gap-1">
              <span className="gscore-kicker">{t("reports")}</span>
              <h2 className="text-lg font-bold text-slate-950 dark:text-white">
                {t("levelReport")}
              </h2>
              <p className="text-sm text-slate-500 dark:text-emerald-100/60">
                {t("levelReportDesc")}
              </p>
            </div>
            <div className="h-[360px]">
              {dashboardLoading ? (
                <Skeleton height="100%" borderRadius="8px" />
              ) : (
                <Chart type="bar" data={chartData} options={chartOptions} />
              )}
            </div>
          </Card>
        </section>

        <Card id="top-group-a" className="gscore-card">
          <div className="mb-4 flex flex-col gap-1 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <span className="gscore-kicker">{t("topGroupA")}</span>
              <h2 className="text-lg font-bold text-slate-950 dark:text-white">
                {t("topGroupA")}
              </h2>
            </div>
            <Tag value={groupALabel} severity="info" rounded />
          </div>
          <DataTable
            value={topGroupA}
            loading={dashboardLoading}
            emptyMessage={t("noData")}
            stripedRows
            showGridlines={false}
            className="gscore-table"
          >
            <Column
              header="#"
              body={(_, options) => options.rowIndex + 1}
              style={{ width: "64px" }}
            />
            <Column field="registrationNumber" header={t("registrationNumber")} sortable />
            <Column
              field="math"
              header={t("math")}
              body={(row: TopGroupAStudentResponse) => formatScore(row.math)}
              sortable
            />
            <Column
              field="physics"
              header={t("physics")}
              body={(row: TopGroupAStudentResponse) => formatScore(row.physics)}
              sortable
            />
            <Column
              field="chemistry"
              header={t("chemistry")}
              body={(row: TopGroupAStudentResponse) => formatScore(row.chemistry)}
              sortable
            />
            <Column
              field="total"
              header={t("total")}
              body={renderTotalScore}
              sortable
            />
          </DataTable>
        </Card>
      </div>
    </>
  );
}

function MetricCard({
  icon,
  label,
  value,
  loading,
  tone,
}: Readonly<{
  icon: string;
  label: string;
  value: string;
  loading: boolean;
  tone: "emerald" | "blue" | "coral";
}>) {
  return (
    <Card className="gscore-card">
      <div className="flex items-center gap-4">
        <div className={`gscore-metric-icon gscore-metric-${tone}`}>
          <i className={`pi ${icon}`} />
        </div>
        <div className="min-w-0">
          <div className="text-sm font-semibold text-slate-500 dark:text-emerald-100/60">
            {label}
          </div>
          {loading ? (
            <Skeleton width="8rem" height="2rem" className="mt-2" />
          ) : (
            <div className="mt-1 text-2xl font-black text-slate-950 dark:text-white">
              {value}
            </div>
          )}
        </div>
      </div>
    </Card>
  );
}

function ScoreMeta({ label, value }: Readonly<{ label: string; value: string }>) {
  return (
    <div>
      <div className="text-xs font-semibold uppercase text-slate-500 dark:text-emerald-100/60">
        {label}
      </div>
      <div className="mt-1 font-bold text-slate-950 dark:text-white">{value}</div>
    </div>
  );
}

function ScoreSkeleton() {
  return (
    <div className="space-y-3">
      <Skeleton height="7rem" borderRadius="8px" />
      <div className="grid grid-cols-2 gap-2 sm:grid-cols-3">
        {Array.from({ length: 9 }).map((_, index) => (
          <Skeleton key={index} height="4rem" borderRadius="8px" />
        ))}
      </div>
    </div>
  );
}

function renderTotalScore(row: TopGroupAStudentResponse) {
  return (
    <strong className="text-emerald-700 dark:text-emerald-200">
      {formatScore(row.total)}
    </strong>
  );
}

function getSubjectName(subject: SubjectLevelStatsResponse, language: Language) {
  return language === "vi" ? subject.nameVi : subject.nameEn;
}

function getErrorMessage(error: Error, fallback: string) {
  return error.message === "Failed to fetch" ? fallback : error.message || fallback;
}

function formatNumber(value: number | undefined) {
  return typeof value === "number" ? new Intl.NumberFormat().format(value) : "--";
}

function formatScore(value: number | null | undefined) {
  return typeof value === "number" ? value.toFixed(2).replace(/\.00$/, "") : "--";
}
