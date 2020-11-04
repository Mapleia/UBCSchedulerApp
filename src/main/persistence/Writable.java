package persistence;

import org.json.JSONObject;

// Interface for classes that can be written to a file.
// Section and User implements this.
public interface Writable {
    JSONObject toJson();
}
