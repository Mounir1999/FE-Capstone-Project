import { Button } from "@/components/ui/button";

const categories = ["Music", "Podcasts", "Audiobooks"];

export function CategoryButtons() {
  return (
    <div className="flex gap-2">
      {categories.map((category) => (
        <Button
          key={category}
          variant="secondary"
          className="hover:bg-accent hover:text-accent-foreground transition-colors"
        >
          {category}
        </Button>
      ))}
    </div>
  );
}