package kikaha.mojo.sample;

import kikaha.mojo.sample.Visit;

public class User {
   @Visit
   String name;
   @Visit
   long id;

   @Visit
   public String getName() {
      return this.name;
   }
}
