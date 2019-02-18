package net.frodwith.jaque.data;

public final class CellMeta implements TruffleObject {
  private final Dashboard dashboard;
  private final FormulaParser parser;
  private final Cell cell;

  public CellMeta(Dashboard dashboard, FormulaParser parser, Cell cell) {
    this.dashboard = dashboard;
    this.parser = parser;
    this.cell = cell;
  }

  public boolean hasMug() {
    return cell.hasMug();
  }

  public boolean hasFunction() {
    return cell.hasFunction();
  }

  public boolean hasObject() {
    return cell.hasObject(dashboard);
  }

  public boolean hasBattery() {
    return cell.hasBattery(dashboard);
  }

  public int getMug() {
    return cell.getMug();
  }

  public NockFunction getFunction() {
    return cell.getFunction(parser);
  }

  public NockObject getObject() {
    return cell.getObject(dashboard);
  }

  public Battery getBattery() {
    return cell.getBattery(dashboard);
  }

  @Override
  public ForeignAccess getForeignAccess() {
    return CellMetaMessageResolutionForeign.ACCESS;
  }
}
