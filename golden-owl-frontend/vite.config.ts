import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";
import svgr from "vite-plugin-svgr";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    svgr({
      svgrOptions: {
        icon: true,
        // This will transform your SVG to a React component
        exportType: "named",
        namedExport: "ReactComponent",
      },
    }),
  ],
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          const normalizedId = id.replace(/\\/g, "/");

          if (!normalizedId.includes("/node_modules/")) {
            return undefined;
          }

          if (
            normalizedId.includes("/apexcharts/") ||
            normalizedId.includes("/react-apexcharts/")
          ) {
            return "charts";
          }

          if (normalizedId.includes("/primereact/")) {
            return "prime";
          }

          return "vendor";
        },
      },
    },
  },
  test: {
    environment: "jsdom",
    environmentOptions: {
      jsdom: {
        url: "http://localhost",
      },
    },
    globals: true,
    setupFiles: "./src/setupTests.ts",
    coverage: {
      provider: "v8",
      reporter: ["text", "lcov", "html"],
      reportsDirectory: "coverage",
      include: [
        "src/api/scores.ts",
        "src/components/common/LanguageSwitcher.tsx",
        "src/context/LanguageContext.tsx",
        "src/context/ThemeContext.tsx",
        "src/pages/Dashboard/Home.tsx",
      ],
      exclude: ["src/**/*.test.ts", "src/**/*.test.tsx", "src/setupTests.ts"],
      thresholds: {
        branches: 100,
        functions: 100,
        lines: 100,
        statements: 100,
      },
    },
  },
});
