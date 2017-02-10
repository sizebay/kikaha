package ibratan.beholder.data;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 1},
   bv = {1, 0, 0},
   k = 1,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0012\u0018\u00002\u00020\u0001BA\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\u0002\u0010\bR\u001c\u0010\u0005\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\n\"\u0004\b\u000e\u0010\fR\u001c\u0010\u0006\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\n\"\u0004\b\u0010\u0010\fR\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\n\"\u0004\b\u0012\u0010\fR\u001c\u0010\u0007\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\n\"\u0004\b\u0014\u0010\f¨\u0006\u0015"},
   d2 = {"Libratan/beholder/data/Address;", "", "street", "", "neighborhood", "city", "state", "zipCode", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getCity", "()Ljava/lang/String;", "setCity", "(Ljava/lang/String;)V", "getNeighborhood", "setNeighborhood", "getState", "setState", "getStreet", "setStreet", "getZipCode", "setZipCode", "beholder"}
)
public final class Address {
   @Nullable
   private String street;
   @Nullable
   private String neighborhood;
   @Nullable
   private String city;
   @Nullable
   private String state;
   @Nullable
   private String zipCode;

   @Nullable
   public final String getStreet() {
      return this.street;
   }

   public final void setStreet(@Nullable String var1) {
      this.street = var1;
   }

   @Nullable
   public final String getNeighborhood() {
      return this.neighborhood;
   }

   public final void setNeighborhood(@Nullable String var1) {
      this.neighborhood = var1;
   }

   @Nullable
   public final String getCity() {
      return this.city;
   }

   public final void setCity(@Nullable String var1) {
      this.city = var1;
   }

   @Nullable
   public final String getState() {
      return this.state;
   }

   public final void setState(@Nullable String var1) {
      this.state = var1;
   }

   @Nullable
   public final String getZipCode() {
      return this.zipCode;
   }

   public final void setZipCode(@Nullable String var1) {
      this.zipCode = var1;
   }

   public Address(@Nullable String street, @Nullable String neighborhood, @Nullable String city, @Nullable String state, @Nullable String zipCode) {
      this.street = street;
      this.neighborhood = neighborhood;
      this.city = city;
      this.state = state;
      this.zipCode = zipCode;
   }

   public Address() {
      this((String)null, (String)null, (String)null, (String)null, (String)null, 31, (DefaultConstructorMarker)null);
   }
}
