import { Link } from "react-router";
import LanguageSwitcher from "../components/common/LanguageSwitcher";
import { ThemeToggleButton } from "../components/common/ThemeToggleButton";
import { useLanguage } from "../context/LanguageContext";
import { useSidebar } from "../context/SidebarContext";

const AppHeader: React.FC = () => {
  const { isMobileOpen, toggleSidebar, toggleMobileSidebar } = useSidebar();
  const { t } = useLanguage();

  const handleToggle = () => {
    if (window.innerWidth >= 1024) {
      toggleSidebar();
    } else {
      toggleMobileSidebar();
    }
  };

  return (
    <header className="sticky top-0 z-99999 flex w-full border-b border-emerald-900/10 bg-white/90 backdrop-blur-xl dark:border-white/10 dark:bg-[#101916]/85">
      <div className="flex w-full items-center justify-between gap-3 px-4 py-3 lg:px-6">
        <div className="flex min-w-0 items-center gap-3">
          <button
            type="button"
            className="z-99999 flex h-11 w-11 items-center justify-center rounded-lg border border-emerald-200 bg-white text-emerald-800 shadow-theme-xs transition-colors hover:bg-emerald-50 dark:border-emerald-400/20 dark:bg-white/[0.05] dark:text-emerald-100 dark:hover:bg-white/[0.08]"
            onClick={handleToggle}
            aria-label="Toggle sidebar"
          >
            <i className={`pi ${isMobileOpen ? "pi-times" : "pi-bars"}`} />
          </button>

          <Link to="/" className="flex min-w-0 items-center gap-3">
            <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-emerald-700 text-lg font-black text-white shadow-theme-sm">
              G
            </span>
            <span className="min-w-0">
              <span className="block truncate text-base font-bold text-slate-950 dark:text-white">
                {t("appName")}
              </span>
              <span className="block truncate text-xs font-medium text-slate-500 dark:text-emerald-100/60">
                {t("appSubtitle")}
              </span>
            </span>
          </Link>
        </div>

        <div className="flex shrink-0 items-center gap-2">
          <LanguageSwitcher />
          <ThemeToggleButton />
        </div>
      </div>
    </header>
  );
};

export default AppHeader;
