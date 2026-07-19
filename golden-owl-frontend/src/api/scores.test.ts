import { afterEach, describe, expect, it, vi } from "vitest";
import { scoresApi } from "./scores";

const jsonResponse = (body: unknown, ok = true, statusText = "OK") =>
  ({
    ok,
    statusText,
    json: vi.fn().mockResolvedValue(body),
  }) as unknown as Response;

describe("scoresApi", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("fetches a score by registration number", async () => {
    const fetchMock = vi.fn().mockResolvedValue(jsonResponse({ registrationNumber: "01000001" }));
    vi.stubGlobal("fetch", fetchMock);

    await expect(scoresApi.getScore("01000001")).resolves.toEqual({
      registrationNumber: "01000001",
    });
    expect(fetchMock).toHaveBeenCalledWith("http://localhost:8080/api/scores/01000001");
  });

  it("fetches score level reports", async () => {
    const report = { importedRecords: 2, subjects: [] };
    vi.stubGlobal("fetch", vi.fn().mockResolvedValue(jsonResponse(report)));

    await expect(scoresApi.getScoreLevels()).resolves.toEqual(report);
  });

  it("fetches top Group A students with the requested limit", async () => {
    const fetchMock = vi.fn().mockResolvedValue(jsonResponse([]));
    vi.stubGlobal("fetch", fetchMock);

    await scoresApi.getTopGroupA(25);

    expect(fetchMock).toHaveBeenCalledWith("http://localhost:8080/api/reports/top-group-a?limit=25");
  });

  it("uses the API error message when one is returned", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(jsonResponse({ message: "Score not found" }, false, "Not Found")),
    );

    await expect(scoresApi.getScore("01999999")).rejects.toThrow("Score not found");
  });

  it("uses the HTTP status when a JSON error has no message", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(jsonResponse({}, false, "Bad Request")),
    );

    await expect(scoresApi.getScore("abc")).rejects.toThrow("Bad Request");
  });

  it("falls back to the HTTP status when the error body is not JSON", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue({
        ok: false,
        statusText: "Service Unavailable",
        json: vi.fn().mockRejectedValue(new Error("not json")),
      }),
    );

    await expect(scoresApi.getScoreLevels()).rejects.toThrow("Service Unavailable");
  });
});
