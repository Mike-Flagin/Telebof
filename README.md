# <p align="center">Telebo<i>f</i></p>
<p align="center">Easy and modern Java Telegram bot API</p>

* [Installation](#installation)
* [Your First Echo Bot](#your-first-echo-bot)
* [Available Types](#available-types) 
* [Available Methods](#available-methods)
* [Handling Updates](#handling-updates)
* [Filtering Updates](#filtering-updates)
* [Types of Handlers](#types-of-handlers)
* [Markup](#markup)
    * [Reply Keyboard Markup](#replymarkup)
    * [Inline Keyboard Markup](#inlinekeyboardmarkup)
    * [Remove Reply Keyboard](#removereplykeyboard)
    * [Force Reply](#forcereply)
* [Inline Bot](#inline-bot)
* [Using Webhook](#using-webhook)
* [Advanced Usages](advanced-usages)
  * [Local Bot API Server](#local-bot-api-server)
  * [Logging](#logging)
  * [Proxy](#proxy)
* [Error Handling](#error-handling)
  

## Installation

* Maven 

```xml
<dependecy>
    <groupId>et.telebof</groupId>
    <artifactId>telegrambot</artifactId>
    <version>1.6.0</version>
</dependecy>

```


### Your First Echo Bot

```java
import et.telebof.BotClient;

public class MyFirstEchoBot {
  static final String TOKEN = "YOUR BOT TOKEN HERE";

  public static void main(String[] args) {
    final BotClient bot = new BotClient(TOKEN);

    // Listening for /start command
    bot.onMessage(filter -> filter.commands("start"), (context, message) -> {
        context.reply("Welcome!").exec();
    });

    // Listening for any text
    bot.onMessage(filter -> filter.TEXT, (context, message) -> {
        context.reply(message.text).exec();
    });

    bot.start(); // finally run the bot
  }
}
```
**Do not worry if you do not understand what the above code mean, it will be explained in the next chapter.** 

## Available Types
All Telegram types are defined in `et.telebof.types`. And they are completely the same as Telegram types. 

Their set method is their camelCased name method

## Available Methods
All Telegram methods are defined in `et.telebof.request` and implemented in `et.telebof.BotContext` class.

These methods can be used in 2 ways: Inside handler using `context` parameter and Outside handler using `bot.context` instance.

##### Inside handler
No need to pass `chat_id` or `user_id` to the methods need it as argument 

##### Outside handler
`chat_id` or `user_id` must be passed to the methods need it as argument 

```java
/* Inside Handler */

// send message
context.sendMessage("Hello, World").exec(); // or
context.sendMessage(message.chat.id, "Hello, World").exec();

// send Photo
context.sendPhoto(new File("photo.png")).exec(); // or
context.sendPhoto(message.chat.id, new File("photo.png")).exec();


/* Outside Handler */

bot.context.sendMessage(123456789L, "Hello, World").exec();
bot.context.sendPhoto(123456789L, new File("photo.png")).exec();
```
**Assume that in our examples it is inside handler**

## Handling Updates
### Update
Update is an event that bot receives like incoming messages, pressing button.

Updates are handled by [registering one or more callback classes](#types-of-handlers). 

Each update has its own handler. These handlers take two parameters as argument: filter class 
and callback class. The filter class is a lambda class of `et.telebof.filter.FilterExecutor` takes `et.telebof.filter.Filter`
as an argument and returns `Boolean`, so that if the condition of this filter 
matches with the update sent from telegram, the callback class will be called and its body gets execute.

The callback class takes two parameters: `BotContext` class and type of class of an update which is being handled

Let's back to the first `echo bot` example.

```java
import et.telebof.BotClient;

public class MyFirstEchoBot {
  static final String TOKEN = "YOUR BOT TOKEN HERE";

  public static void main(String[] args) {
    final BotClient bot = new BotClient(TOKEN);

    bot.onMessage(filter -> filter.commands("start"), (context, message) -> {
      context.reply("Welcome!").exec();
    });

    bot.onMessage(filter -> filter.TEXT, (context, message) -> {
      context.reply(message.text).exec();
    });

    bot.start();
  }
}
```
We have two handlers: `/start` command handler and `text` handler.

- The first handler handles `/start` command and send back a text `Welcome!`.
- The second handler handles any incoming **text** and echoes the text.
- The `reply` method is a shortage of `sendMessage` and replies message to the message.

- `exec()` meaning `execute` is an enclosing and request sender method. This means before ending and sending request, you can pass 
optional parameters and then send a request to telegram. For example `sendMessage` method has optional parameters 
`parse_mode`, `reply_markup`. So you can pass their value for these parameters and send request to telegram.

```java
import et.telebof.enums.ParseMode;

context.sendMessage("*Hello, World*")
        .parseMode(ParseMode.MARKDOWN)
        .exec();
```
Lastly we start our bot by using `start()` which does not take any parameter and run our bot via **long polling.** 

**IMPORTANT: All handlers are handled in the order in which they were registered.**

## Types of Handlers
There are 18 types of updates to be handled

#### Message Handler
```java
bot.onMessage((context, message) -> {}); 
```
#### CallbackQuery handler
```java
bot.onCallback((context, callback) -> {});
```
#### InlineQuery Handler
```java
bot.onInline((context, query) -> {} );
```
#### Poll Handler
```java
bot.onPoll((context, poll) -> {});
```
#### PollAnswer Handler
```java
bot.onPoll((context, poll_answer) -> {});
```
#### ShippingQuery Handler
```java
bot.onShipping((context, shipping) -> {});
```
#### ChannelPost Handler
```java
bot.onChannelPost((context, channel_post) -> {});
```
#### PreCheckoutQuery Handler
```java
bot.onPreCheckout((context, pre_checkout) -> {});
```
#### EditedMessage Handler
```java
bot.onEditedMessage((context, edited_message) -> {});
```
#### EditedChannelPost Handler
```java
bot.onEditedChannelPost((context, edited_c) -> {});
```
#### MyChatMember Handler
```java
bot.onMychatMember((context, my_chat) -> {});
```
#### ChatMember Handler
```java
bot.onChatMember((context, chat_member) -> {});
```
#### ChosenInlineResult Handler
```java
bot.onChosenInlineResult((context, chosen) -> {});
```
#### MessageReaction Handler
```java
bot.onReaction((context, reaction) -> {});
```
#### MessageReactionCount Handler
```java
bot.onReactionCount((context, reaction_count) -> {});
```
#### ChatBoost Handler
```java
bot.onChatBoost((context, chat_boost) -> {});
```
#### RemovedChatBoost Handler
```java
bot.onRemovedChatBoost((context, removed_chat_boost) -> {});
```
**If only callback class is passed to a handler, the filter class will return `true` by default**

```java
bot.onMessage((context, message) -> {});
```
The same as
```java
bot.onMessage(filter -> true, (context, message) -> {});
```

## Filtering Updates

In previous topic we have seen how to create handlers and how they work. In this section we will talk about how filters 
work and how we use them.

As previously discussed, all handlers take two parameters: filter class and callback class. 

The filter class is used for filtering content of updates and separate the same update by content they hold. 

### Predefined Filters 
- `filter.TEXT` - filter message is text  
- `filer.PHOTO` - filter message is photo 
- `filter.VIDEO` - filter message is video
- `filter.VOICE` - filter message is voice
- `filter.AUDIO` - filter message is audio
- `filter.ANIMATION` - filter message is animation
- `filter.DOCUMENT` - filter message is document
- `filter.VIDEO_NOTE` - filter message is video note
- `filter.CONTACT` - filter message is contact
- `filter.LOCATION` - filter message is location
- `filter.GAME` - filter message is game
- `filter.VENUE` - filter message is venue
- `filter.STICKER` - filter message is sticker
- `filter.DICE` - filter message is dice
- `filter.INVOICE` - message is an invoice for a payment
- `filter.MEDIA` - filter message is one of the following: photo, video, audio, sticker, video_note, voice, animation, document.
- `filter.PASSPORT_DATA` - message is Telegram passport data
- `filter.USERS_SHARED` - filter users were shared with the bot
- `filter.CHAT_SHARED` - filter chat was shared with the bot
- `filter.NEW_CHAT_MEMBER` - filter new members joined or added to the group
- `filter.LEFT_CHAT_MEMBER` - filter member left from the group
- `filter.NEW_CHAT_PHOTO` - filter a chat photo was changed
- `filter.NEW_CHAT_TITLE` - filter a chat title was changed
- `filter.GROUP_CHAT_CREATED` - filter a group chat was created
- `filter.SUPERGROUP_CHAT_CREATED` - filter a group chat was created
- `filter.CHANNEL_CHAT_CREATED` - filter a channel was created
- `filter.MESSAGE_AUTO_DELETE_TIMER_CHANGED` - filter auto-delete timer settings changed in the chat
- `filter.MIGRATED` - filter the group/supergroup has been migrated to/from a supergroup/group 
- `filter.PINNED_MESSAGE` - filter a message was pinned
- `filter.SUCCESFULL_PAYMENT` - filter message is about a successful payment
- `filter.PROXIMITY_ALERT_TRIGGERED` - filter a user in the chat triggered another user's proximity alert
- `filter.BOOST_ADDED` - filter user boosted the chat
- `filter.GIVEAWAY` - filter message is scheduled giveaway
- `filter.GIVEWAY_CREATED` - filter a scheduled giveaway was created
- `filter.GIVEAWAY_COMPLETED` -  a giveaway without public winners was completed
- `filter.FORUM_TOPIC_CREATED` - filter forum topic created
- `filter.FORUM_TOPIC_CLOSED` - filter forum topic closed
- `filter.FORUM_TOPIC_EDITED` - filter forum topic edited
- `filter.FORUM_TOPIC_REOPNED` - filter forum topic reopened
- `filter.WEB_APP_DATA` - filter data sent by a Web App
- `filter.VIDEO_CHAT_STARTED` - filter video chat was started in the chat
- `filter.VIDEO_CHAT_ENDED` - filter video chat was ended in the chat
- `filter.VIDEO_CHAT_PARTICIPANT_INVITED` - filter new participants invited to a video chat
- `filter.VIDEO_CHAT_SCHEDULED` - filter video chat scheduled
- `filter.FORWARDED` - filter message was forwarded
- `filter.REPLIED` - filter message was replied to another message
- `filter.REPLIED_TO_STORY` - filter message was replied to chat story
- `filter.ENTITES` - filter message text contains entities(bold, italic, underline, mention, url, hashtag)
- `filter.BOT` - filter user is bot
- `filter.ZERO_INLINE_QUERY` - filter query is empty
- `filter.PRIVATE` - filter the chat is `private`
- `filter.GROUP` - filter the chat type is `group`
- `filter.SUPERGROUP` - filter chat type is `supergroup`
- `filter.CHANNEL` - filter chat type is `channel`
- `filter.commands()` - filter message contains given commands.
- `filter.callbackData()` - filter given callback_data belongs to the pressed button.  
- `filter.inlineQuery()` - filter given query is queried
- `filter.customFilter()` - filter given filter
- `filter.state()` - filter current state is given state. Always true if given state is `*` for filtering current state

```java
// handles incoming texts
bot.onMessage(filter -> filter.TEXT, (context, message) -> {});

// handles incoming photos
bot.onMessage(filter -> filter.PHOTO, (context, message) -> {});


// handles incoming videos
bot.onMessage(filter -> filter.VIDEO, (context, message) -> {});
```
### Combining filters
You may want to handle `text` and `photo` in one handler or a `text` in different chats. To do so use logical operators 
(&&, ||, !) and combine them together. 

Here are some examples

```java
// handles incoming text in private chat
bot.onMessage(filter -> filter.TEXT && filter.PRIVATE, (context, message) -> {});

// handles an incoming text or photo
bot.onMessage(filter -> filter.TEXT || filter.PHOTO, (context, message) -> {});

// handles incoming text in supergroup chat 
bot.onMessage(filter -> filter.TEXT && filter.SUPERGROUP, (context, message) -> {});

// handles incoming audio or video in private chat
bot.onMessage(filter -> filter.PRIVATE && (filter.AUDIO || filter.VIDEO), (context, message) -> {});

```
### Writing your own filter
You can write your own filter using `filter.customFilter`.

This example will show you how you can write filters using `et.telebof.filters.CustomFilter` and `filter.customFilter`.

```java
import et.telebof.BotContext;
import et.telebof.filters.CustomFilter;
import et.telebof.handlers.MessageHandler;
import et.telebof.types.Message;
import et.telebof.types.Update;


// Filter whether the incoming message text is number or not.

class IsNumberFilter implements CustomFilter {
  @Override
  public boolean check(Update update) {
    Message message = update.message;
    try {
      int number = Integer.parseInt(message.text);
      return true; // If it is parsed without any error, then it is number
    } catch (NumberFormatException e) {
      // If the text is not number
      return false;
    }
  }
}

public class FilterBot {

  static void numberFilter(BotContext context, Message message){
      context.sendMessage("It is number").exec();
  }

  public static void main(String[] args) {
      // ...
      bot.onMessage(filter -> filter.TEXT && filter.customFilter(new IsNumberFilter()),
              FilterBot::numberFilter);
  }
}
```

### Advanced Filters

There are some advanced filters for handling `command` , `pressing button`, `inline query`. 
`filter.commands`, `filter.callbackData` and `filter.inlineQuery` respectively.

Example for handling commands using `filter.commands`

```java
// handles /start command
bot.onMessage(filter -> filter.commands("start"), (context, message) -> {});

// handles /help command
bot.onMessage(filter -> filter.commands("help"), (context, message) -> {});
```

Example for handling inline button through its callback data using `filter.callbackData`

```java
// handles inline button which its callback data equals with "a"
bot.onCallback(filter -> filter.callbackData("a"), (context, callback) -> {
    context.answer("You pressed A button!").exec();
});
```

Example for handling inline query using `filter.inlineQuery`
```java
// handles an inline query which its query equals with a word "hello"
bot.onInline(filter -> filter.inlineQuery("hello"), (context, query) -> {});
```

### State Filter
There is another special filter to make conversations with bot called `state filter`.
```java
bot.onMessage(filter -> filter.commands("start"), (context, message) -> {
    context.sendMessage("What is your name?").exec();
    bot.setState(message.from.id, "name"); // set our state to `name`. You can set whatever
});

bot.onMessage(filter -> filter.state("name") && filter.TEXT, (context, message) -> {     
    context.sendMessage(String.format("Your name is %s", message.text)).exec();
    context.clearState(message.from.id);
});
```


## Markups
### ReplyMarkup
Example for using reply markup

```java
ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup()
        .resizeKeyboard(true) // resize keyboard
        
markup.add("A", "B", "C"); // You can add String or 
markup.add("D", "E"); 
markup.add(new KeyboardButton("F")); // KeybaordButton class

context.sendMssage("Hello, World!").replyMarkup(markup).exec();
```

### InlineKeyboardMarkup
example for using InlineKeyboardMarkup

```java
InlineKeybaordMarkup inlineMarkup = new InlineKeybaordMarkup();

inlineMarkup.addKeybaord(
  new InlineKeybaordButton("A").callbackData("a"), 
  new InlineKeybaordButton("C").callbackData("b"), 
  new InlineKeybaordButton("Url").url("www.example.com")
); // 3 keyboards on a row 

// also  possible
InlineKeybaordMarkup inlineMarkup = new InlineKeybaordMarkup(new InlineKeybaordButton[]{
  new InlineKeybaordButton("A").callbackData("a"), 
  new InlineKeybaordButton("B").callbackData("b")
}, 1); // 1 rowWidth

// also possible
InlineKeybaordMarkup inlineMarkup = new InlineKeybaordMarkup(new InlineKeybaordButton[][]{
    new InlineKeybaordButton[]{
      new InlineKeybaordButton("A").callbackData("a"), 
      new InlineKeybaordButton("B").callbackData("b")
    }, // 2 rows
    new InlineKeyboardButton[]{
      new InlineKeyboardButton("Url").url("www.example.com")
    } // 1 row
  } 
);

context.sendMessage("Press one button").replyMarkup(inlineMarkup).exec();
```

### ForceReply
```java
context.sendMessage("Can you tell me your name please?")
        .replyMarkup(new ForceReply())
        .exec();
```

### RemoveReplyKeyboard
```java
context.sendMessage("There is no reply keyboard now")
        .replyMarkup(new RemoveReplyKeybaord())
        .exec(); 
```

## Inline Bot

```java
import et.telebof.types.InlineQueryResult;
import et.telebof.types.InlineQueryResultArticle;
import et.telebof.types.InputTextMessageContent;

public class InlineBot {
  public static void main(String[] args) {
    //...

    bot.onInline(filter -> filter.ZERO_INLINE_QUERY, (context, query) -> {
      InlineQueryResultArticle article = new InlineQueryResultArticle("1")
              .title("Write something")
              .description("click here")
              .inputTextMessageContent(new InputTextMessageContent("Please write something"));

      context.answerInline(new InlineQueryResult[]{article}).exec();
    });

  }
}
```

## Using Webhook

```java
import et.telebof.Webhook;
import java.io.File;

class MyWebhookBot {
  public static void main(String[] args) {
    Webhook webhook = new Webhook("www.example.com", "/bot");  // URL and path respectively
    //...
    bot.deleteWebhook(); // first delete webhook if any
    bot.setWebhook(webhook); // set webhook

    //... 
    bot.start(); // start our bot using webhook

  }
}
```

## Advanced Usage

### Local Bot API Server

```java
import et.telebof.BotClient;

String url = "https://example.com/bot%s/%s";
BotClient bot = new BotClient.Builder(TOKEN)
        .localBotApiUrl(url)
        .build();
```
**You have to log out your bot from the Telegram server before switching to your local API server using `bot.context.logOut().exec()`**

### Logging
log current status of the bot.
```java
import et.telebof.BotClient;

BotClient bot = new BotClient.Builder(TOKEN)
        .log(true)
        .build();
```

### Proxy

```java
import et.telebof.BotClient;

import java.net.InetSocketAddress;
import java.net.Proxy;

InetSocketAddress address = new InetSocketAddress(80, "127.97.91"); // port and hostname respectively 

Proxy proxy = new Proxy(Proxy.Type.SOCKS, address);
BotClient bot = new BotClient
        .Builder(TOKEN)
        .proxy(proxy)
        .build();
```

```java
import et.telebof.BotClient;
import et.telebof.enums.ParseMode;
import et.telebof.enums.Updates;

BotClient bot = new BotClient.Builder(TOKEN)
        .log(true) // Log current status
        .skipOldUpdates(false) // Receive updates sent last 24 hours 
        .parseMode(ParseMode.HTML) // Default parse mode passed to sendXyz methods
        .limit(10) // Limiting how many updates should be received at maximum per request 
        .useTestServer(false) // Using test server
        .timeout(30) // timeout
        .offset(-1) // offset
        .allowedUpdates(Updates.ALL) // Allowed updates
        .proxy(null) // proxy
        .build(); // build our client
```

## Error Handling

```java
import et.telebof.TelegramApiException;

try{     
    context.sendMessage("Hello, World").exec();    
} catch(TelegramApiException apiException){
    System.out.println(apiException.description);
}
```