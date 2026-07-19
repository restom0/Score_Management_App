export type SubjectScore = {
  code: string;
  nameEn: string;
  nameVi: string;
  score: number | null;
};

export type ScoreResponse = {
  registrationNumber: string;
  foreignLanguageCode: string | null;
  groupATotal: number | null;
  scores: SubjectScore[];
};

export type SubjectLevelStatsResponse = {
  code: string;
  nameEn: string;
  nameVi: string;
  excellent: number;
  good: number;
  average: number;
  belowAverage: number;
  missing: number;
};

export type ScoreLevelReportResponse = {
  importedRecords: number;
  subjects: SubjectLevelStatsResponse[];
};

export type TopGroupAStudentResponse = {
  registrationNumber: string;
  math: number;
  physics: number;
  chemistry: number;
  total: number;
};
