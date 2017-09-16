package org.someth2say.taijitu.query;

/**
 * @author Jordi Sola
 */
public class QueryUtilsException extends Exception {
   private static final long serialVersionUID = 5470286417772512577L;

   public QueryUtilsException(String message) {
      super(message);
   }

   public QueryUtilsException(Exception embedded) {
      super(embedded);
   }

   public QueryUtilsException(String message, Exception embedded) {
      super(message, embedded);
   }
}
