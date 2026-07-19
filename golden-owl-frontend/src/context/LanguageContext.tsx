import type React from "react";
import { createContext, useContext, useEffect, useMemo, useState } from "react";

const supportedLanguages = ["en", "vi", "ca", "es", "de", "it", "fr"] as const;

export type Language = (typeof supportedLanguages)[number];

const en = {
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
  invalidRegistrationNumber: "Enter exactly 8 digits.",
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
};

type TranslationMessages = Record<keyof typeof en, string>;

const translations: Record<Language, TranslationMessages> = {
  en,
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
    invalidRegistrationNumber: "Nhập đúng 8 chữ số.",
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
  ca: {
    appName: "G-Scores",
    appSubtitle: "Examen nacional del Vietnam 2024",
    dashboard: "Tauler",
    reports: "Informes",
    topGroupA: "Top Grup A",
    scoreLookup: "Cerca de puntuació",
    registrationNumber: "Número de registre",
    searchPlaceholder: "Introdueix 8 dígits",
    search: "Cerca",
    importedRecords: "Registres importats",
    subjects: "Assignatures",
    completeGroupA: "Grup A complet",
    levelReport: "Informe de nivells",
    levelReportDesc: "Distribució per assignatura: >=8, 6-8, 4-6, <4",
    excellent: "Excel·lent",
    good: "Bo",
    average: "Mitjà",
    belowAverage: "Per sota de la mitjana",
    missing: "Sense nota",
    foreignLanguageCode: "Codi d'idioma",
    groupATotal: "Total Grup A",
    noScoreYet: "Cerca un SBD per veure les puntuacions del candidat.",
    scoreNotFound: "Puntuació no trobada",
    invalidRegistrationNumber: "Introdueix exactament 8 dígits.",
    total: "Total",
    math: "Matemàtiques",
    physics: "Física",
    chemistry: "Química",
    student: "Estudiant",
    noData: "Sense dades",
    loadFailed: "No s'han pogut carregar les dades. Comprova el backend al port 8080.",
    language: "Idioma",
    theme: "Tema",
    light: "Clar",
    dark: "Fosc",
    dataWorkspace: "Espai de puntuacions",
    dataWorkspaceDesc: "Cerca, franges de puntuació i classificació del bloc d'admissió en una consola enfocada.",
  },
  es: {
    appName: "G-Scores",
    appSubtitle: "Examen nacional de Vietnam 2024",
    dashboard: "Panel",
    reports: "Informes",
    topGroupA: "Top Grupo A",
    scoreLookup: "Consulta de notas",
    registrationNumber: "Número de registro",
    searchPlaceholder: "Introduce 8 dígitos",
    search: "Buscar",
    importedRecords: "Registros importados",
    subjects: "Asignaturas",
    completeGroupA: "Grupo A completo",
    levelReport: "Informe de niveles",
    levelReportDesc: "Distribución por asignatura: >=8, 6-8, 4-6, <4",
    excellent: "Excelente",
    good: "Bueno",
    average: "Promedio",
    belowAverage: "Por debajo del promedio",
    missing: "Sin nota",
    foreignLanguageCode: "Código de idioma",
    groupATotal: "Total Grupo A",
    noScoreYet: "Busca un SBD para ver las notas del candidato.",
    scoreNotFound: "Nota no encontrada",
    invalidRegistrationNumber: "Introduce exactamente 8 dígitos.",
    total: "Total",
    math: "Matemáticas",
    physics: "Física",
    chemistry: "Química",
    student: "Estudiante",
    noData: "Sin datos",
    loadFailed: "No se pudieron cargar los datos. Revisa el backend en el puerto 8080.",
    language: "Idioma",
    theme: "Tema",
    light: "Claro",
    dark: "Oscuro",
    dataWorkspace: "Espacio de notas",
    dataWorkspaceDesc: "Consulta, bandas de notas y clasificación del bloque de admisión en una consola enfocada.",
  },
  de: {
    appName: "G-Scores",
    appSubtitle: "Nationale Prüfung Vietnam 2024",
    dashboard: "Dashboard",
    reports: "Berichte",
    topGroupA: "Top Gruppe A",
    scoreLookup: "Notensuche",
    registrationNumber: "Registrierungsnummer",
    searchPlaceholder: "8-stellige SBD eingeben",
    search: "Suchen",
    importedRecords: "Importierte Datensätze",
    subjects: "Fächer",
    completeGroupA: "Vollständige Gruppe A",
    levelReport: "Notenstufenbericht",
    levelReportDesc: "Verteilung nach Fach: >=8, 6-8, 4-6, <4",
    excellent: "Ausgezeichnet",
    good: "Gut",
    average: "Durchschnitt",
    belowAverage: "Unter Durchschnitt",
    missing: "Fehlt",
    foreignLanguageCode: "Sprachcode",
    groupATotal: "Summe Gruppe A",
    noScoreYet: "Suche eine SBD, um Kandidatennoten zu sehen.",
    scoreNotFound: "Note nicht gefunden",
    invalidRegistrationNumber: "Genau 8 Ziffern eingeben.",
    total: "Summe",
    math: "Mathematik",
    physics: "Physik",
    chemistry: "Chemie",
    student: "Kandidat",
    noData: "Keine Daten",
    loadFailed: "Daten konnten nicht geladen werden. Prüfe das Backend auf Port 8080.",
    language: "Sprache",
    theme: "Design",
    light: "Hell",
    dark: "Dunkel",
    dataWorkspace: "Notenarbeitsbereich",
    dataWorkspaceDesc: "Suche, Notenbänder und Rangliste des Zulassungsblocks in einer fokussierten Konsole.",
  },
  it: {
    appName: "G-Scores",
    appSubtitle: "Esame nazionale del Vietnam 2024",
    dashboard: "Dashboard",
    reports: "Report",
    topGroupA: "Top Gruppo A",
    scoreLookup: "Ricerca punteggio",
    registrationNumber: "Numero di registrazione",
    searchPlaceholder: "Inserisci 8 cifre",
    search: "Cerca",
    importedRecords: "Record importati",
    subjects: "Materie",
    completeGroupA: "Gruppo A completo",
    levelReport: "Report livelli punteggio",
    levelReportDesc: "Distribuzione per materia: >=8, 6-8, 4-6, <4",
    excellent: "Eccellente",
    good: "Buono",
    average: "Medio",
    belowAverage: "Sotto la media",
    missing: "Mancante",
    foreignLanguageCode: "Codice lingua",
    groupATotal: "Totale Gruppo A",
    noScoreYet: "Cerca un SBD per vedere i punteggi del candidato.",
    scoreNotFound: "Punteggio non trovato",
    invalidRegistrationNumber: "Inserisci esattamente 8 cifre.",
    total: "Totale",
    math: "Matematica",
    physics: "Fisica",
    chemistry: "Chimica",
    student: "Candidato",
    noData: "Nessun dato",
    loadFailed: "Impossibile caricare i dati. Controlla il backend sulla porta 8080.",
    language: "Lingua",
    theme: "Tema",
    light: "Chiaro",
    dark: "Scuro",
    dataWorkspace: "Area punteggi",
    dataWorkspaceDesc: "Ricerca, fasce di punteggio e classifica del blocco di ammissione in una console focalizzata.",
  },
  fr: {
    appName: "G-Scores",
    appSubtitle: "Examen national du Vietnam 2024",
    dashboard: "Tableau de bord",
    reports: "Rapports",
    topGroupA: "Top Groupe A",
    scoreLookup: "Recherche de score",
    registrationNumber: "Numéro d'inscription",
    searchPlaceholder: "Saisir 8 chiffres",
    search: "Rechercher",
    importedRecords: "Enregistrements importés",
    subjects: "Matières",
    completeGroupA: "Groupe A complet",
    levelReport: "Rapport des niveaux",
    levelReportDesc: "Répartition par matière : >=8, 6-8, 4-6, <4",
    excellent: "Excellent",
    good: "Bon",
    average: "Moyen",
    belowAverage: "Sous la moyenne",
    missing: "Manquant",
    foreignLanguageCode: "Code de langue",
    groupATotal: "Total Groupe A",
    noScoreYet: "Recherchez un SBD pour voir les scores du candidat.",
    scoreNotFound: "Score introuvable",
    invalidRegistrationNumber: "Saisissez exactement 8 chiffres.",
    total: "Total",
    math: "Mathématiques",
    physics: "Physique",
    chemistry: "Chimie",
    student: "Candidat",
    noData: "Aucune donnée",
    loadFailed: "Impossible de charger les données. Vérifiez le backend sur le port 8080.",
    language: "Langue",
    theme: "Thème",
    light: "Clair",
    dark: "Sombre",
    dataWorkspace: "Espace des scores",
    dataWorkspaceDesc: "Recherche, tranches de score et classement du bloc d'admission dans une console ciblée.",
  },
};

export type TranslationKey = keyof typeof en;

type LanguageContextValue = {
  language: Language;
  setLanguage: (language: Language) => void;
  t: (key: TranslationKey) => string;
};

const LanguageContext = createContext<LanguageContextValue | undefined>(undefined);

function isSupportedLanguage(value: string | null): value is Language {
  return supportedLanguages.includes(value as Language);
}

export const LanguageProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [selectedLanguage, setSelectedLanguage] = useState<Language>("en");

  useEffect(() => {
    const savedLanguage = localStorage.getItem("language");
    if (isSupportedLanguage(savedLanguage)) {
      setSelectedLanguage(savedLanguage);
    }
  }, []);

  const setLanguage = (nextLanguage: Language) => {
    localStorage.setItem("language", nextLanguage);
    setSelectedLanguage(nextLanguage);
  };

  const value = useMemo(
    () => ({
      language: selectedLanguage,
      setLanguage,
      t: (key: TranslationKey) => translations[selectedLanguage][key],
    }),
    [selectedLanguage],
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
