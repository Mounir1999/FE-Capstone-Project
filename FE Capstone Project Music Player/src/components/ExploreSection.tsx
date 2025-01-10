import { Card, CardContent } from "@/components/ui/card";

const playlists = [
  {
    title: "Summer Hits",
    description: "Top tracks for summer",
    imageUrl: "https://picsum.photos/200/200?random=6",
  },
  {
    title: "Rock Classics",
    description: "Timeless rock songs",
    imageUrl: "https://picsum.photos/200/200?random=7",
  },
  {
    title: "Jazz & Blues",
    description: "Smooth jazz collection",
    imageUrl: "https://picsum.photos/200/200?random=8",
  },
  {
    title: "Electronic Mix",
    description: "Best electronic tracks",
    imageUrl: "https://picsum.photos/200/200?random=9",
  },
  {
    title: "Acoustic Sessions",
    description: "Unplugged favorites",
    imageUrl: "https://picsum.photos/200/200?random=10",
  },
  {
    title: "Hip Hop Essentials",
    description: "Must-hear hip hop",
    imageUrl: "https://picsum.photos/200/200?random=11",
  },
];

export function ExploreSection() {
  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-semibold">Explore Playlists</h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4">
        {playlists.map((playlist) => (
          <Card key={playlist.title} className="cursor-pointer hover:bg-accent/5 transition-colors">
            <CardContent className="p-4">
              <img
                src={playlist.imageUrl}
                alt={playlist.title}
                className="aspect-square w-full rounded-md object-cover"
              />
              <div className="mt-3">
                <h3 className="font-semibold">{playlist.title}</h3>
                <p className="text-sm text-muted-foreground">{playlist.description}</p>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}