import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, describe, expect, it } from "vitest";
import { LanguageProvider, useLanguage } from "./LanguageContext";

function LanguageProbe() {
  const { language, setLanguage, t } = useLanguage();

  return (
    <div>
      <span data-testid="language">{language}</span>
      <span data-testid="search-label">{t("search")}</span>
      <button type="button" onClick={() => setLanguage("fr")}>
        switch
      </button>
    </div>
  );
}

function MissingProviderProbe() {
  useLanguage();
  return null;
}

describe("LanguageContext", () => {
  afterEach(() => {
    localStorage.clear();
  });

  it("defaults to English and changes language through the provider API", async () => {
    const user = userEvent.setup();
    render(
      <LanguageProvider>
        <LanguageProbe />
      </LanguageProvider>,
    );

    expect(screen.getByTestId("language")).toHaveTextContent("en");
    await user.click(screen.getByRole("button", { name: "switch" }));

    expect(screen.getByTestId("language")).toHaveTextContent("fr");
    expect(screen.getByTestId("search-label")).toHaveTextContent("Rechercher");
    expect(localStorage.getItem("language")).toBe("fr");
  });

  it("hydrates a saved supported language", async () => {
    localStorage.setItem("language", "de");
    render(
      <LanguageProvider>
        <LanguageProbe />
      </LanguageProvider>,
    );

    await waitFor(() => expect(screen.getByTestId("language")).toHaveTextContent("de"));
    expect(screen.getByTestId("search-label")).toHaveTextContent("Suchen");
  });

  it("ignores unsupported saved languages", async () => {
    localStorage.setItem("language", "jp");
    render(
      <LanguageProvider>
        <LanguageProbe />
      </LanguageProvider>,
    );

    await waitFor(() => expect(screen.getByTestId("language")).toHaveTextContent("en"));
  });

  it("requires consumers to be rendered inside a provider", () => {
    expect(() => render(<MissingProviderProbe />)).toThrow(
      "useLanguage must be used within a LanguageProvider",
    );
  });
});
