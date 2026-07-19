import { Link, useLocation } from "react-router";
import { useLanguage } from "../context/LanguageContext";
import { useSidebar } from "../context/SidebarContext";
import SidebarWidget from "./SidebarWidget";

type NavItem = {
  nameKey: "dashboard" | "reports" | "topGroupA";
  icon: string;
  path: string;
};

const navItems: NavItem[] = [
  {
    icon: "pi-chart-bar",
    nameKey: "dashboard",
    path: "/",
  },
  {
    icon: "pi-sliders-h",
    nameKey: "reports",
    path: "/#reports",
  },
  {
    icon: "pi-trophy",
    nameKey: "topGroupA",
    path: "/#top-group-a",
  },
];

const AppSidebar: React.FC = () => {
  const { isExpanded, isMobileOpen, isHovered, setIsHovered } = useSidebar();
  const location = useLocation();
  const { t } = useLanguage();
  const expanded = isExpanded || isHovered || isMobileOpen;

  return (
    <aside
      className={`fixed left-0 top-0 z-50 mt-16 flex h-screen flex-col border-r border-emerald-900/10 bg-white px-4 text-slate-950 transition-all duration-300 ease-in-out dark:border-white/10 dark:bg-[#101916] lg:mt-0
        ${
          isExpanded || isMobileOpen
            ? "w-[290px]"
            : isHovered
              ? "w-[290px]"
              : "w-[90px]"
        }
        ${isMobileOpen ? "translate-x-0" : "-translate-x-full"}
        lg:translate-x-0`}
      onMouseEnter={() => !isExpanded && setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <div
        className={`flex py-7 ${
          !isExpanded && !isHovered ? "lg:justify-center" : "justify-start"
        }`}
      >
        <Link to="/" className="flex items-center gap-3">
          <span className="flex h-10 w-10 items-center justify-center rounded-lg bg-emerald-700 text-lg font-black text-white shadow-theme-sm">
            G
          </span>
          {expanded && (
            <span>
              <span className="block text-lg font-bold">{t("appName")}</span>
              <span className="block text-xs font-semibold uppercase text-slate-400">
                {t("appSubtitle")}
              </span>
            </span>
          )}
        </Link>
      </div>
      <div className="flex flex-col overflow-y-auto duration-300 ease-linear no-scrollbar">
        <nav className="mb-6">
          <ul className="flex flex-col gap-2">
            {navItems.map((item) => {
              const active = location.pathname === "/" && item.path === "/";
              return (
                <li key={item.nameKey}>
                  <Link
                    to={item.path}
                    className={`menu-item group ${
                      active ? "menu-item-active" : "menu-item-inactive"
                    } ${expanded ? "justify-start" : "lg:justify-center"}`}
                  >
                    <i className={`pi ${item.icon} text-lg`} />
                    {expanded && <span>{t(item.nameKey)}</span>}
                  </Link>
                </li>
              );
            })}
          </ul>
        </nav>
        {expanded ? <SidebarWidget /> : null}
      </div>
    </aside>
  );
};

export default AppSidebar;
