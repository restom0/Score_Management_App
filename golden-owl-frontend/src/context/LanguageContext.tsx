import type React from "react";
import { createContext, useContext, useEffect, useMemo, useState } from "react";

export type Language = "en" | "vi";

const translations = {
  en: {
    appName: "G-Scores",
    appSubtitle: "Vietnam National Exam 2024",
    dashboard: "Dashboard",
    reports: "Reports",
    topGroupA: "Top Group A",
    scoreLookup: "Score lookup",
    registrationNumber: "Registration number",
    searchPlaceholder: "Enter 8-digit SBD",
    search: "Search",
    importedRecords: "Imported records",
    subjects: "Subjects",
    completeGroupA: "Complete Group A",
    levelReport: "Score level report",
    levelReportDesc: "Distribution by subject: >=8, 6-8, 4-6, <4",
    excellent: "Excellent",
    good: "Good",
    average: "Average",
    belowAverage: "Below average",
    missing: "Missing",
    foreignLanguageCode: "Language code",
    groupATotal: "Group A total",
    noScoreYet: "Search one SBD to view candidate scores.",
    scoreNotFound: "Score not found",
    total: "Total",
    math: "Math",
    physics: "Physics",
    chemistry: "Chemistry",
    student: "Student",
    noData: "No data",
    loadFailed: "Could not load data. Check backend on port 8080.",
    language: "Language",
    theme: "Theme",
    light: "Light",
    dark: "Dark",
    dataWorkspace: "Score workspace",
    dataWorkspaceDesc: "Lookup, score bands, and admission block leaderboard in one focused console.",
  },
  vi: {
    appName: "G-Scores",
    appSubtitle: "Điểm thi THPT 2024",
    dashboard: "Bảng điều khiển",
    reports: "Báo cáo",
    topGroupA: "Top khối A",
    scoreLookup: "Tra cứu điểm",
    registrationNumber: "Số báo danh",
    searchPlaceholder: "Nhập SBD 8 chữ số",
    search: "Tra cứu",
    importedRecords: "Bản ghi đã nạp",
    subjects: "Môn thi",
    completeGroupA: "Đủ khối A",
    levelReport: "Thống kê mức điểm",
    levelReportDesc: "Phân bố theo môn: >=8, 6-8, 4-6, <4",
    excellent: "Giỏi",
    good: "Khá",
    average: "Trung bình",
    belowAverage: "Dưới TB",
    missing: "Không thi",
    foreignLanguageCode: "Mã ngoại ngữ",
    groupATotal: "Tổng khối A",
    noScoreYet: "Nhập một SBD để xem điểm thí sinh.",
    scoreNotFound: "Không tìm thấy điểm",
    total: "Tổng",
    math: "Toán",
    physics: "Vật lí",
    chemistry: "Hóa học",
    student: "Thí sinh",
    noData: "Không có dữ liệu",
    loadFailed: "Không tải được dữ liệu. Kiểm tra backend cổng 8080.",
    language: "Ngôn ngữ",
    theme: "Giao diện",
    light: "Sáng",
    dark: "Tối",
    dataWorkspace: "Không gian điểm thi",
    dataWorkspaceDesc: "Tra cứu, thống kê mức điểm, và bảng xếp hạng khối A trong một màn hình.",
  },
} as const;

export type TranslationKey = keyof typeof translations.en;

type LanguageContextValue = {
  language: Language;
  setLanguage: (language: Language) => void;
  t: (key: TranslationKey) => string;
};

const LanguageContext = createContext<LanguageContextValue | undefined>(undefined);

export const LanguageProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [language, setLanguageState] = useState<Language>("en");

  useEffect(() => {
    const savedLanguage = localStorage.getItem("language") as Language | null;
    if (savedLanguage === "en" || savedLanguage === "vi") {
      setLanguageState(savedLanguage);
    }
  }, []);

  const setLanguage = (nextLanguage: Language) => {
    localStorage.setItem("language", nextLanguage);
    setLanguageState(nextLanguage);
  };

  const value = useMemo(
    () => ({
      language,
      setLanguage,
      t: (key: TranslationKey) => translations[language][key],
    }),
    [language],
  );

  return (
    <LanguageContext.Provider value={value}>
      {children}
    </LanguageContext.Provider>
  );
};

export const useLanguage = () => {
  const context = useContext(LanguageContext);
  if (context === undefined) {
    throw new Error("useLanguage must be used within a LanguageProvider");
  }
  return context;
};
