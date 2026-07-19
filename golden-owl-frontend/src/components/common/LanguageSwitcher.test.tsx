import type React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { LanguageProvider } from "../../context/LanguageContext";
import LanguageSwitcher from "./LanguageSwitcher";

vi.mock("primereact/dropdown", () => ({
  Dropdown: ({
    value,
    onChange,
    options,
    valueTemplate,
    itemTemplate,
    "aria-label": ariaLabel,
  }: {
    value: string;
    onChange: (event: { value: string }) => void;
    options: Array<{ label: string; nativeName: string; value: string }>;
    valueTemplate: (option?: { label: string; nativeName: string; value: string }) => React.ReactNode;
    itemTemplate: (option?: { label: string; nativeName: string; value: string }) => React.ReactNode;
    "aria-label": string;
  }) => (
    <div>
      <div data-testid="selected-language">{valueTemplate(options.find((option) => option.value === value))}</div>
      <div>{itemTemplate(undefined)}</div>
      <select aria-label={ariaLabel} value={value} onChange={(event) => onChange({ value: event.target.value })}>
        <option value="">Empty</option>
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
      <div>{options.map((option) => itemTemplate(option))}</div>
    </div>
  ),
}));

describe("LanguageSwitcher", () => {
  it("renders all supported language choices and changes language", () => {
    render(
      <LanguageProvider>
        <LanguageSwitcher />
      </LanguageProvider>,
    );

    const dropdown = screen.getByLabelText("Language");
    expect(screen.getByTestId("selected-language")).toHaveTextContent("English");
    expect(screen.getByRole("option", { name: "EN" })).toBeInTheDocument();
    expect(screen.getByRole("option", { name: "VI" })).toBeInTheDocument();
    expect(screen.getByRole("option", { name: "CA" })).toBeInTheDocument();
    expect(screen.getByRole("option", { name: "ES" })).toBeInTheDocument();
    expect(screen.getByRole("option", { name: "DE" })).toBeInTheDocument();
    expect(screen.getByRole("option", { name: "IT" })).toBeInTheDocument();
    expect(screen.getByRole("option", { name: "FR" })).toBeInTheDocument();

    fireEvent.change(dropdown, { target: { value: "es" } });
    expect(localStorage.getItem("language")).toBe("es");

    fireEvent.change(dropdown, { target: { value: "" } });
    expect(localStorage.getItem("language")).toBe("es");
  });
});
