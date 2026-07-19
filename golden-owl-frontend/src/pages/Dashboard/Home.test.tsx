import React from "react";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { scoresApi } from "../../api/scores";
import { AppWrapper } from "../../components/common/PageMeta";
import { LanguageProvider } from "../../context/LanguageContext";
import { ThemeProvider } from "../../context/ThemeContext";
import type { TopGroupAStudentResponse } from "../../types/scores";
import Home from "./Home";

vi.mock("../../api/scores", () => ({
  scoresApi: {
    getScore: vi.fn(),
    getScoreLevels: vi.fn(),
    getTopGroupA: vi.fn(),
  },
}));

vi.mock("primereact/button", () => ({
  Button: ({
    type = "button",
    label,
    disabled,
    loading,
  }: {
    type?: "button" | "submit" | "reset";
    label?: string;
    disabled?: boolean;
    loading?: boolean;
  }) => (
    <button type={type} disabled={disabled}>
      {loading ? "Loading" : label}
    </button>
  ),
}));

vi.mock("primereact/card", () => ({
  Card: ({ children, id, className }: { children: React.ReactNode; id?: string; className?: string }) => (
    <section id={id} className={className}>
      {children}
    </section>
  ),
}));

vi.mock("primereact/chart", () => ({
  Chart: ({ data, options }: { data: { labels?: string[] }; options: unknown }) => (
    <div data-testid="chart" data-options={JSON.stringify(options)}>
      {data.labels?.join(",")}
    </div>
  ),
}));

type ColumnProps = {
  field?: keyof TopGroupAStudentResponse;
  header?: React.ReactNode;
  body?: (row: TopGroupAStudentResponse, options: { rowIndex: number }) => React.ReactNode;
};

vi.mock("primereact/column", () => ({
  Column: () => null,
}));

vi.mock("primereact/datatable", () => ({
  DataTable: ({
    children,
    emptyMessage,
    loading,
    value,
  }: {
    children: React.ReactNode;
    emptyMessage: string;
    loading?: boolean;
    value: TopGroupAStudentResponse[];
  }) => {
    const columns = React.Children.toArray(children).filter(React.isValidElement<ColumnProps>);

    if (loading) {
      return <div role="status">Loading table</div>;
    }

    return (
      <table>
        <thead>
          <tr>
            {columns.map((column, index) => (
              <th key={`header-${index}`}>{column.props.header}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {value.length === 0 ? (
            <tr>
              <td>{emptyMessage}</td>
            </tr>
          ) : (
            value.map((row, rowIndex) => (
              <tr key={row.registrationNumber}>
                {columns.map((column, columnIndex) => (
                  <td key={`${row.registrationNumber}-${columnIndex}`}>
                    {column.props.body
                      ? column.props.body(row, { rowIndex })
                      : column.props.field
                        ? row[column.props.field]
                        : null}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>
    );
  },
}));

vi.mock("primereact/inputtext", () => ({
  InputText: ({
    value,
    onChange,
    placeholder,
    maxLength,
  }: {
    value: string;
    onChange: React.ChangeEventHandler<HTMLInputElement>;
    placeholder?: string;
    maxLength?: number;
  }) => <input value={value} onChange={onChange} placeholder={placeholder} maxLength={maxLength} />,
}));

vi.mock("primereact/message", () => ({
  Message: ({ text }: { text: string }) => <div role="alert">{text}</div>,
}));

vi.mock("primereact/skeleton", () => ({
  Skeleton: () => <div data-testid="skeleton" />,
}));

vi.mock("primereact/tag", () => ({
  Tag: ({ value }: { value: string }) => <span>{value}</span>,
}));

const report = {
  importedRecords: 1234,
  subjects: [
    {
      code: "math",
      nameEn: "Math",
      nameVi: "Toán",
      excellent: 2,
      good: 1,
      average: 0,
      belowAverage: 0,
      missing: 0,
    },
  ],
};

const topGroupA = [
  {
    registrationNumber: "01000003",
    math: 9,
    physics: 9.5,
    chemistry: 9.5,
    total: 28,
  },
];

const candidateScore = {
  registrationNumber: "01000001",
  foreignLanguageCode: "N1",
  groupATotal: 19.65,
  scores: [
    { code: "math", nameEn: "Math", nameVi: "Toán", score: 8 },
    { code: "physics", nameEn: "Physics", nameVi: "Vật lí", score: 6.25 },
    { code: "chemistry", nameEn: "Chemistry", nameVi: "Hóa học", score: null },
  ],
};

const candidateScoreWithoutLanguage = {
  ...candidateScore,
  foreignLanguageCode: null,
};

const mockedScoresApi = vi.mocked(scoresApi);

function renderHome() {
  return render(
    <AppWrapper>
      <ThemeProvider>
        <LanguageProvider>
          <Home />
        </LanguageProvider>
      </ThemeProvider>
    </AppWrapper>,
  );
}

describe("Home", () => {
  beforeEach(() => {
    mockedScoresApi.getScoreLevels.mockResolvedValue(report);
    mockedScoresApi.getTopGroupA.mockResolvedValue(topGroupA);
    mockedScoresApi.getScore.mockResolvedValue(candidateScore);
  });

  afterEach(() => {
    localStorage.clear();
    document.documentElement.classList.remove("dark");
    vi.clearAllMocks();
  });

  it("loads dashboard data and searches for a candidate score", async () => {
    const user = userEvent.setup();
    renderHome();

    await screen.findByText("Score workspace");
    await waitFor(() => expect(mockedScoresApi.getScoreLevels).toHaveBeenCalled());
    expect(screen.getByText("1,234")).toBeInTheDocument();
    expect(screen.getByText("01000003")).toBeInTheDocument();
    expect(screen.getByText("28")).toBeInTheDocument();

    await user.clear(screen.getByPlaceholderText("Enter 8-digit SBD"));
    await user.type(screen.getByPlaceholderText("Enter 8-digit SBD"), "01000001");
    await user.click(screen.getByRole("button", { name: "Search" }));

    await screen.findByText("Student");
    expect(mockedScoresApi.getScore).toHaveBeenCalledWith("01000001");
    expect(screen.getByText("N1")).toBeInTheDocument();
    expect(screen.getByText("19.65")).toBeInTheDocument();
    expect(screen.getAllByText("Chemistry").length).toBeGreaterThan(1);
    expect(screen.getByText("--")).toBeInTheDocument();
  });

  it("sanitizes the registration number and blocks invalid searches", async () => {
    const user = userEvent.setup();
    renderHome();
    const input = screen.getByPlaceholderText("Enter 8-digit SBD");
    const searchButton = screen.getByRole("button", { name: "Search" });

    await user.clear(input);
    expect(searchButton).toBeDisabled();
    fireEvent.submit(input.closest("form") as HTMLFormElement);

    expect(await screen.findByRole("alert")).toHaveTextContent("Enter exactly 8 digits.");

    await user.type(input, "abc123456789");
    expect(input).toHaveValue("12345678");
    expect(searchButton).toBeEnabled();
  });

  it("shows a friendly dashboard load failure message", async () => {
    mockedScoresApi.getScoreLevels.mockRejectedValue(new Error("Failed to fetch"));

    renderHome();

    expect(await screen.findByRole("alert")).toHaveTextContent(
      "Could not load data. Check backend on port 8080.",
    );
    expect(screen.getByText("Search one SBD to view candidate scores.")).toBeInTheDocument();
  });

  it("shows API search errors and clears the candidate result", async () => {
    const user = userEvent.setup();
    mockedScoresApi.getScore.mockRejectedValue(new Error("Score not found"));
    renderHome();

    await user.click(screen.getByRole("button", { name: "Search" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("Score not found");
    expect(screen.queryByText("Student")).not.toBeInTheDocument();
  });

  it("falls back when search errors have an empty message", async () => {
    const user = userEvent.setup();
    mockedScoresApi.getScore.mockRejectedValue(new Error(""));
    renderHome();

    await user.click(screen.getByRole("button", { name: "Search" }));

    expect(await screen.findByRole("alert")).toHaveTextContent(
      "Could not load data. Check backend on port 8080.",
    );
  });

  it("renders empty metadata and ignores late dashboard failures after unmount", async () => {
    let rejectReport!: (error: Error) => void;
    const reportPromise = new Promise<typeof report>((_, reject) => {
      rejectReport = reject;
    });
    mockedScoresApi.getScoreLevels.mockReturnValue(reportPromise);
    mockedScoresApi.getScore.mockResolvedValue(candidateScoreWithoutLanguage);
    const { unmount } = renderHome();

    unmount();
    rejectReport(new Error("late failure"));
    await reportPromise.catch(() => undefined);

    mockedScoresApi.getScoreLevels.mockResolvedValue(report);
    renderHome();
    await userEvent.click(screen.getByRole("button", { name: "Search" }));
    expect(await screen.findByText("Student")).toBeInTheDocument();
    expect(screen.getAllByText("--").length).toBeGreaterThan(1);
  });

  it("renders Vietnamese labels and dark chart options from saved preferences", async () => {
    const user = userEvent.setup();
    localStorage.setItem("language", "vi");
    localStorage.setItem("theme", "dark");
    renderHome();

    expect(await screen.findByText("Không gian điểm thi")).toBeInTheDocument();
    expect(await screen.findByTestId("chart")).toHaveTextContent("Toán");
    expect(screen.getByTestId("chart").dataset.options).toContain("#d6f5ea");

    await user.click(screen.getByRole("button", { name: "Tra cứu" }));
    expect(await screen.findByText("Thí sinh")).toBeInTheDocument();
    expect(screen.getAllByText("Toán").length).toBeGreaterThan(1);
  });
});
