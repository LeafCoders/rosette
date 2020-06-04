package se.leafcoders.rosette.core.permission;

import se.leafcoders.rosette.endpoint.article.ArticlePermissionValue;
import se.leafcoders.rosette.endpoint.articleserie.ArticleSeriePermissionValue;
import se.leafcoders.rosette.endpoint.articletype.ArticleTypePermissionValue;
import se.leafcoders.rosette.endpoint.asset.AssetPermissionValue;
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolderPermissionValue;
import se.leafcoders.rosette.endpoint.event.EventPermissionValue;
import se.leafcoders.rosette.endpoint.eventtype.EventTypePermissionValue;
import se.leafcoders.rosette.endpoint.group.GroupPermissionValue;
import se.leafcoders.rosette.endpoint.message.MessagePermissionValue;
import se.leafcoders.rosette.endpoint.permission.PermissionPermissionValue;
import se.leafcoders.rosette.endpoint.permissionset.PermissionSetPermissionValue;
import se.leafcoders.rosette.endpoint.podcast.PodcastPermissionValue;
import se.leafcoders.rosette.endpoint.resource.ResourcePermissionValue;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypePermissionValue;
import se.leafcoders.rosette.endpoint.slideshow.SlidePermissionValue;
import se.leafcoders.rosette.endpoint.slideshow.SlideShowPermissionValue;
import se.leafcoders.rosette.endpoint.user.UserPermissionValue;

public class PermissionType {

    public static ArticleSeriePermissionValue articleSeries() {
        return new ArticleSeriePermissionValue();
    }

    public static ArticlePermissionValue articles() {
        return new ArticlePermissionValue();
    }

    public static ArticleTypePermissionValue articleTypes() {
        return new ArticleTypePermissionValue();
    }

    public static AssetFolderPermissionValue assetFolders() {
        return new AssetFolderPermissionValue();
    }

    public static AssetPermissionValue assets() {
        return new AssetPermissionValue();
    }

    public static EventPermissionValue events() {
        return new EventPermissionValue();
    }

    public static EventTypePermissionValue eventTypes() {
        return new EventTypePermissionValue();
    }

    public static GroupPermissionValue groups() {
        return new GroupPermissionValue();
    }

    public static MessagePermissionValue messages() {
        return new MessagePermissionValue();
    }

    public static PermissionPermissionValue permissions() {
        return new PermissionPermissionValue();
    }

    public static PermissionSetPermissionValue permissionSets() {
        return new PermissionSetPermissionValue();
    }

    public static PodcastPermissionValue podcasts() {
        return new PodcastPermissionValue();
    }

    public static ResourcePermissionValue resources() {
        return new ResourcePermissionValue();
    }

    public static ResourceTypePermissionValue resourceTypes() {
        return new ResourceTypePermissionValue();
    }

    public static SlidePermissionValue slides() {
        return new SlidePermissionValue();
    }

    public static SlideShowPermissionValue slideShows() {
        return new SlideShowPermissionValue();
    }

    public static UserPermissionValue users() {
        return new UserPermissionValue();
    }
}
