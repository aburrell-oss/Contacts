package com.griddynamics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/** Entry point for the Contacts application. */
public final class Main {

  /** Loads a contacts file if provided, then
   * starts the interactive application. */
  public void start() {
    Contacts contacts = new Contacts();
    new ContactsApp(contacts).run();
  }

  /**
   * Application entry point.
   *
   * @param args optional single argument specifying
   *             filename to load
   */
  public static void main(final String[] args) {
    Contacts contacts = new Contacts();

    if (args.length > 0) {
      final String filename = args[0];
      final File file = new File(filename);

      if (file.exists()) {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(file))) {
          Object obj = in.readObject();

          // Validate the object is actually a Contacts instance
          if (obj instanceof Contacts) {
            contacts = (Contacts) obj;
            contacts.setFilename(filename);
          } else {
            System.out.println("Cannot load file.");
          }
        } catch (IOException | ClassNotFoundException e) {
          System.out.println("Cannot load file.");
        }
      }
    }

    new ContactsApp(contacts).run();
  }
}
