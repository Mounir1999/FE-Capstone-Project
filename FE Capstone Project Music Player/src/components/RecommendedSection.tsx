import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";
import { Card, CardContent } from "@/components/ui/card";

const recommendedItems = [
  {
    title: "Daily Mix 1",
    description: "Tailored for you",
    imageUrl: "https://picsum.photos/200/200?random=1",
  },
  {
    title: "Discover Weekly",
    description: "New music for you",
    imageUrl: "https://picsum.photos/200/200?random=2",
  },
  {
    title: "Chill Vibes",
    description: "Relaxing playlist",
    imageUrl: "https://picsum.photos/200/200?random=3",
  },
  {
    title: "Top Hits",
    description: "Popular right now",
    imageUrl: "https://picsum.photos/200/200?random=4",
  },
  {
    title: "Indie Mix",
    description: "Fresh indie tracks",
    imageUrl: "https://picsum.photos/200/200?random=5",
  },
];

export function RecommendedSection() {
  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-semibold">Recommended for You</h2>
      <ScrollArea className="w-full whitespace-nowrap rounded-md">
        <div className="flex w-full gap-4 pb-4">
          {recommendedItems.map((item) => (
            <Card key={item.title} className="w-[200px] shrink-0 cursor-pointer hover:bg-accent/5 transition-colors">
              <CardContent className="p-4">
                <img
                  src={item.imageUrl}
                  alt={item.title}
                  className="aspect-square w-full rounded-md object-cover"
                />
                <div className="mt-3">
                  <h3 className="font-semibold">{item.title}</h3>
                  <p className="text-sm text-muted-foreground">{item.description}</p>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
        <ScrollBar orientation="horizontal" />
      </ScrollArea>
    </div>
  );
}