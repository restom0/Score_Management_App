import { Dropdown } from "primereact/dropdown";
import type { Language } from "../../context/LanguageContext";
import { useLanguage } from "../../context/LanguageContext";

type LanguageOption = {
  label: string;
  nativeName: string;
  value: Language;
};

const languageOptions: LanguageOption[] = [
  { label: "EN", nativeName: "English", value: "en" },
  { label: "VI", nativeName: "Tiếng Việt", value: "vi" },
  { label: "CA", nativeName: "Català", value: "ca" },
  { label: "ES", nativeName: "Español", value: "es" },
  { label: "DE", nativeName: "Deutsch", value: "de" },
  { label: "IT", nativeName: "Italiano", value: "it" },
  { label: "FR", nativeName: "Français", value: "fr" },
];

export default function LanguageSwitcher() {
  const { language, setLanguage, t } = useLanguage();

  const renderLanguage = (option?: LanguageOption) => {
    if (!option) return null;

    return (
      <div className="gscore-language-option">
        <span className="gscore-language-code">{option.label}</span>
        <span className="gscore-language-name">{option.nativeName}</span>
      </div>
    );
  };

  return (
    <div className="flex items-center gap-2">
      <span className="hidden text-xs font-semibold uppercase text-slate-500 dark:text-emerald-100/60 sm:block">
        {t("language")}
      </span>
      <Dropdown
        value={language}
        onChange={(event) => event.value && setLanguage(event.value as Language)}
        options={languageOptions}
        optionLabel="label"
        optionValue="value"
        valueTemplate={(option) => renderLanguage(option)}
        itemTemplate={(option) => renderLanguage(option)}
        aria-label={t("language")}
        className="gscore-language-dropdown"
        panelClassName="gscore-language-panel"
      />
    </div>
  );
}
