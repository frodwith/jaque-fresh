package net.frodwith.jaque.runtime;

public final class CoreFinder  {
  private final Cache<Cell,LocationResult> cache;

  public CoreFinder() {
    this.cache = CacheBuilder.newBuilder().weakKeys().build();
  }

  private static final class FineStep {
    public final Cell battery;
    public final Axis axis;

    public FineStep(Cell battery, Axis axis) {
      this.battery = battery;
    }
  }

  public static final class LocationResult {
    public final Location location;
    private final FineStep[] steps;
    private final Object root;

    @ExplodeLoop
    public boolean check(Object core) {
      try { 
        for ( FineStep step : steps ) {
          if ( !Equality.equals(step.battery, Cell.require(core).head) ) {
            return false;
          }
          core = step.axis.fragment(core);
        }
        return Equality.equals(root, core);
      }
      catch ( CellRequiredException e ) {
        return false;
      }
    }
  }

  public LocationResult locate(Cell core) {
    LocationResult cached = cache.getIfPresent(core.head);
    if ( (null == cached) || !cached.check(core) ) {
      cached = slow(core);
      cache.put(core.head, cached);
    }
    return cached;
  }

  private LocationResult slow(Cell core) {

  }
}
