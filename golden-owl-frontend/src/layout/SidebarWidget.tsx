import { useLanguage } from "../context/LanguageContext";

export default function SidebarWidget() {
  const { t } = useLanguage();

  return (
    <div className="mx-auto mb-10 w-full max-w-60 rounded-lg border border-emerald-900/10 bg-emerald-50 px-4 py-5 dark:border-emerald-300/10 dark:bg-white/[0.04]">
      <div className="mb-3 flex h-9 w-9 items-center justify-center rounded-lg bg-coral-500 text-white">
        <i className="pi pi-database" />
      </div>
      <h3 className="mb-2 font-semibold text-slate-950 dark:text-white">
        {t("dataWorkspace")}
      </h3>
      <p className="text-theme-sm text-slate-600 dark:text-emerald-100/70">
        {t("dataWorkspaceDesc")}
      </p>
    </div>
  );
}
