import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "primereact/resources/themes/lara-light-teal/theme.css";
import "primereact/resources/primereact.min.css";
import "primeicons/primeicons.css";
import "swiper/swiper-bundle.css";
import "flatpickr/dist/flatpickr.css";
import "./index.css";
import App from "./App.tsx";
import { AppWrapper } from "./components/common/PageMeta.tsx";
import { ThemeProvider } from "./context/ThemeContext.tsx";
import { LanguageProvider } from "./context/LanguageContext.tsx";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ThemeProvider>
      <LanguageProvider>
        <AppWrapper>
          <App />
        </AppWrapper>
      </LanguageProvider>
    </ThemeProvider>
  </StrictMode>,
);
