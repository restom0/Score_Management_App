import type {
  ScoreLevelReportResponse,
  ScoreResponse,
  TopGroupAStudentResponse,
} from "../types/scores";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api";

async function getJson<T>(path: string): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`);
  if (!response.ok) {
    let message = response.statusText;
    try {
      const body = (await response.json()) as { message?: string };
      message = body.message ?? message;
    } catch {
      // Empty or non-JSON API errors fall back to HTTP status text.
    }
    throw new Error(message);
  }
  return (await response.json()) as T;
}

export const scoresApi = {
  getScore(registrationNumber: string) {
    return getJson<ScoreResponse>(`/scores/${registrationNumber}`);
  },
  getScoreLevels() {
    return getJson<ScoreLevelReportResponse>("/reports/score-levels");
  },
  getTopGroupA(limit = 10) {
    return getJson<TopGroupAStudentResponse[]>(`/reports/top-group-a?limit=${limit}`);
  },
};
