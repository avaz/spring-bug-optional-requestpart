package spring;

/**
 *
 * @author Anderson
 */
public class Item {
  private String name;

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }
  
}
