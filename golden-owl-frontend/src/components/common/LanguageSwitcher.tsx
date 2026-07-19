import { SelectButton } from "primereact/selectbutton";
import type { Language } from "../../context/LanguageContext";
import { useLanguage } from "../../context/LanguageContext";

const languageOptions: { label: string; value: Language }[] = [
  { label: "EN", value: "en" },
  { label: "VI", value: "vi" },
];

export default function LanguageSwitcher() {
  const { language, setLanguage, t } = useLanguage();

  return (
    <div className="flex items-center gap-2">
      <span className="hidden text-xs font-semibold uppercase text-slate-500 dark:text-emerald-100/60 sm:block">
        {t("language")}
      </span>
      <SelectButton
        value={language}
        onChange={(event) => event.value && setLanguage(event.value)}
        options={languageOptions}
        allowEmpty={false}
        aria-label={t("language")}
        className="gscore-language-switcher"
      />
    </div>
  );
}
