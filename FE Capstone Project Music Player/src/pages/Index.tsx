import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/AppSidebar";
import { SearchBar } from "@/components/SearchBar";
import { CategoryButtons } from "@/components/CategoryButtons";
import { RecommendedSection } from "@/components/RecommendedSection";
import { ExploreSection } from "@/components/ExploreSection";

const Index = () => {
  return (
    <SidebarProvider>
      <div className="min-h-screen flex w-full">
        <AppSidebar />
        <main className="flex-1 p-6 space-y-6">
          <div className="flex items-center justify-between gap-4 flex-wrap">
            <SearchBar />
            <CategoryButtons />
          </div>
          <RecommendedSection />
          <ExploreSection />
        </main>
      </div>
    </SidebarProvider>
  );
};

export default Index;