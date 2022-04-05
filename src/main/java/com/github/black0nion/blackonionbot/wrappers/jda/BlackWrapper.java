package com.github.black0nion.blackonionbot.wrappers.jda;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public abstract class BlackWrapper {

  protected abstract Document getIdentifier();

  protected abstract MongoCollection<Document> getCollection();

  @Nullable
  public Document getConfig() {
    return getCollection().find(getIdentifier()).first();
  }

  public <T> void saveList(final String key, final List<T> value) {
    save(new Document(key, value));
  }

  public <T> T get(final String key, final Class<T> clazz) {
    return Optional.ofNullable(getConfig()).map(doc -> doc.get(key, clazz)).orElse(null);
  }

  public <T> void save(final String key, final T value) {
    save(new Document(key, value));
  }

  private void save(final Document doc) {
    if (getCollection().find(getIdentifier()).first() == null) {
      final Document newDoc = getIdentifier();
      newDoc.putAll(doc);
      getCollection().insertOne(newDoc);
    } else {
      getCollection().updateOne(getIdentifier(), new Document("$set", doc));
    }
  }

  public void clear(final String... keys) {
    final Document doc = new Document();
    for (final String key : keys) {
      doc.put(key, "");
    }
    clear(doc);
  }

  public void clear(final Document doc) {
    getCollection().updateOne(getIdentifier(), new Document("$unset", doc));
  }
}
