package com.github.black0nion.blackonionbot.wrappers.jda.impls;

import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackWrapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.List;

public abstract class UserImpl extends BlackWrapper implements User {

  protected final User user;

  protected UserImpl(User user) {
    this.user = user;
  }

  @Override
  @NotNull
  public String getName() {
    return this.user.getName();
  }

  @Override
  @NotNull
  public String getDiscriminator() {
    return this.user.getDiscriminator();
  }

  @Override
  public String getAvatarId() {
    return this.user.getAvatarId();
  }

  @Override
  public String getAvatarUrl() {
    return this.user.getAvatarUrl();
  }

  @Override
  @NotNull
  public String getDefaultAvatarId() {
    return this.user.getDefaultAvatarId();
  }

  @Override
  @NotNull
  public String getDefaultAvatarUrl() {
    return this.user.getDefaultAvatarUrl();
  }

  @Override
  @NotNull
  public String getEffectiveAvatarUrl() {
    return this.user.getEffectiveAvatarUrl();
  }

  @NotNull
  @Override
  public RestAction<Profile> retrieveProfile() {
    return this.user.retrieveProfile();
  }

  @Override
  @NotNull
  public String getAsTag() {
    return this.user.getAsTag();
  }

  @Override
  public boolean hasPrivateChannel() {
    return this.user.hasPrivateChannel();
  }

  @Override
  @NotNull
  public RestAction<PrivateChannel> openPrivateChannel() {
    return this.user.openPrivateChannel();
  }

  @Override
  @NotNull
  public List<Guild> getMutualGuilds() {
    return this.user.getMutualGuilds();
  }

  @Override
  public boolean isBot() {
    return this.user.isBot();
  }

  @Override
  @NotNull
  public JDA getJDA() {
    return this.user.getJDA();
  }

  @NotNull
  @Override
  public EnumSet<UserFlag> getFlags() {
    return this.user.getFlags();
  }

  @Override
  public int getFlagsRaw() {
    return this.user.getFlagsRaw();
  }

  @Override
  @NotNull
  public String getAsMention() {
    return this.user.getAsMention();
  }

  @Override
  public void formatTo(final Formatter formatter, final int flags, final int width,
      final int precision) {
    this.user.formatTo(formatter, flags, width, precision);
  }

  @Override
  @NotNull
  public String getId() {
    return this.user.getId();
  }

  @Override
  public long getIdLong() {
    return this.user.getIdLong();
  }

  @Override
  @NotNull
  public OffsetDateTime getTimeCreated() {
    return this.user.getTimeCreated();
  }

  @Override
  public boolean isSystem() {
    return this.user.isSystem();
  }

  @Nonnull
  public String getFullName() {
    return this.getName() + "#" + this.getDiscriminator();
  }

  public String getFullNameEscaped() {
    return Utils.escapeMarkdown(this.getName()) + "#" + this.getDiscriminator();
  }
}
