package et.telebof.requests;

import com.google.gson.reflect.TypeToken;
import et.telebof.BotLog;
import et.telebof.Util;
import et.telebof.types.InputMedia;
import et.telebof.types.Markup;
import et.telebof.types.Message;

import java.lang.reflect.Type;
import java.util.List;


public class SendMediaGroup extends DefaultParameters<SendMediaGroup, List<Message>> {

    public static Type responseType = TypeToken.getParameterized(List.class, Message.class).getType();
    public <T extends InputMedia> SendMediaGroup(Object chatId, T[] medias, RequestSender requestSender) {
        super(chatId, requestSender, "sendMediaGroup", responseType);
        add("media", medias);
        for (InputMedia im: medias){
            if (im.isFile()){
                add(im.getInputFile().file.getName(), im.getInputFile().file);
                setContentType(im.getInputFile().contentType);
                setHasMultipart(true);
            }
        }

    }

    @Override
    @Deprecated(forRemoval = true)
    public SendMediaGroup replyMarkup(Markup replyMarkup) {
        BotLog.warn("`sendMediaGroup` method does not support replyMarkup option.");
        return this;
    }


}
