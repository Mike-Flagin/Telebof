package et.telebof.requests;

import et.telebof.enums.ParseMode;
import et.telebof.Util;
import et.telebof.types.InlineKeyboardMarkup;
import et.telebof.types.Message;
import et.telebof.types.MessageEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EditMessageCaption extends AbstractBaseRequest<EditMessageCaption, Message> {
    public EditMessageCaption(Object chatId, int messageId, RequestSender requestSender) {
        super(chatId, requestSender, "editMessageCaption", Message.class);
        add("message_id", messageId);
    }

    public EditMessageCaption(String inlineMessageId, RequestSender requestSender) {
        super(requestSender, "editMessageCaption", Message.class);
        add("inline_message_id", inlineMessageId);
    }

    public EditMessageCaption parseMode(@NotNull ParseMode parseMode) {
        return add("parse_mode",  parseMode);
    }

    public EditMessageCaption entities(MessageEntity[] captionEntities) {
        return add("caption_entities", List.of(captionEntities));
    }

    public EditMessageCaption replyMarkup(InlineKeyboardMarkup replyMarkup) {
        return add("reply_markup", replyMarkup);
    }

    public EditMessageCaption showCaptionAboveMedia(boolean show_caption_above_media){
        return add("show_caption_above_media", show_caption_above_media);
    }
}
