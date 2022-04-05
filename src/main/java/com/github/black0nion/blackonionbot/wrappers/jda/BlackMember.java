package com.github.black0nion.blackonionbot.wrappers.jda;

import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.wrappers.jda.impls.MemberImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.entities.Member;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BlackMember extends MemberImpl {

  private static final MongoCollection<Document> warnsCollection =
      MongoDB.DATABASE.getCollection("warns");

  private static final LoadingCache<Member, BlackMember> members =
      CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build(new CacheLoader<>() {
        @Nonnull
        @Override
        public BlackMember load(@Nonnull final Member member) {
          final BlackGuild from = BlackGuild.from(member.getGuild());
          return new BlackMember(member, from);
        }
      });

  public static BlackMember from(final Member member) {
    try {
      return members.get(member);
    } catch (final Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static BlackMember from(final Member member, final BlackGuild guild) {
    try {
      return members.get(member, () -> new BlackMember(member, guild));
    } catch (final Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Reloadable("membercache")
  public static void clearCache() {
    members.invalidateAll();
  }

  private final BlackGuild blackGuild;
  private final List<Warn> warns = new ArrayList<>();

  private BlackMember(@NotNull final Member member, final BlackGuild blackGuild) {
    super(member);
    this.blackGuild = blackGuild;

    this.save("username", this.member.getEffectiveName());
    this.save("guildname", blackGuild.getName());
    ImmutableList.copyOf(warnsCollection.find(this.getIdentifier())).stream().map(doc -> {
      if (doc.containsKey("reason") && doc.containsKey("issuer") && doc.containsKey("date"))
        return new Warn(doc.getLong("guildid"), doc.getLong("issuer"), doc.getLong("userid"),
            doc.getLong("date"), doc.getString("reason"));
      else if (doc.containsKey("issuer") && doc.containsKey("date"))
        return new Warn(doc.getLong("guildid"), doc.getLong("issuer"), doc.getLong("userid"),
            doc.getLong("date"));
      return null;
    }).filter(Objects::nonNull).forEach(this.warns::add);
    this.blackUser = BlackUser.from(this.member.getUser());
  }

  private final BlackUser blackUser;

  public BlackUser getBlackUser() {
    return blackUser;
  }

  public void warn(final Warn w) {
    this.warns.add(w);
    final Document doc = new Document();
    doc.putAll(this.getIdentifier());
    doc.put("issuer", w.issuer());
    final long l = w.date();
    doc.put("date", l);
    if (w.reason() != null) {
      doc.put("reason", w.reason());
    }
    warnsCollection.insertOne(doc);
  }

  public void deleteWarn(final Warn w) {
    this.warns.remove(w);
    warnsCollection.deleteOne(this.getIdentifier().append("date", w.date()));
  }

  // TODO: check why it's never used
  public void saveWarns() {
    warnsCollection.insertMany(this.warns.stream().map(warn -> {
      final Document doc = new Document();
      doc.put("issuer", warn.issuer());
      doc.putAll(this.getIdentifier());
      if (warn.reason() != null) {
        doc.put("reason", warn.reason());
      }
      return doc;
    }).collect(Collectors.toList()));
  }

  public List<Warn> getWarns() {
    return this.warns;
  }

  @Override
  public Document getIdentifier() {
    return new Document().append("guildid", this.blackGuild.getIdLong()).append("userid",
        this.member.getIdLong());
  }

  private static final MongoCollection<Document> configs =
      MongoDB.DATABASE.getCollection("membersettings");

  @Override
  protected MongoCollection<Document> getCollection() {
    return configs;
  }

  @Override
  public String toString() {
    return "BlackMember{" + "blackGuild=" + blackGuild + ", warns=" + warns + ", blackUser="
        + blackUser + '}';
  }
}
