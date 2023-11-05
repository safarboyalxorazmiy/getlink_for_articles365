package com.alcode.service;

import com.alcode.config.BotConfig;
import com.alcode.user.UsersService;
import com.alcode.userFollowers.UserFollowerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final UsersService usersService;
    private final UserFollowerService userFollowerService;

    public TelegramBot(BotConfig config, UsersService usersService, UserFollowerService userFollowerService) {
        this.config = config;
        this.usersService = usersService;
        this.userFollowerService = userFollowerService;

        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Boshlash"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error during setting bot's command list: {}", e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            if (update.getMessage().getChat().getType().equals("supergroup")) {
                // DO NOTHING CHANNEL CHAT ID IS -1001764816733
                return;
            } else {
                if (update.hasMessage() && update.getMessage().hasText()) {
                    String messageText = update.getMessage().getText();

                    if (messageText.startsWith("/")) {
                        switch (messageText) {
                            case "/start" -> {
                                usersService.createUser(
                                        update.getMessage().getFrom().getId(),
                                        update.getMessage().getFrom().getFirstName(),
                                        update.getMessage().getFrom().getLastName()
                                );

                                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                                InlineKeyboardButton btn = new InlineKeyboardButton();
                                rowInLine = new ArrayList<>();
                                btn = new InlineKeyboardButton();
                                btn.setText("A’zo bo’ldim");
                                btn.setCallbackData("/check");
                                rowInLine.add(btn);
                                rows.add(rowInLine);

                                inlineKeyboardMarkup.setKeyboard(rows);

                                SendPhoto message = new SendPhoto();
                                message.setChatId(chatId);
                                message.setPhoto(new InputFile(new java.io.File("wallpaper.jpg"), "wallpaper.jpg"));
                                message.setCaption("<b>3 000 000 sum</b>\uD83D\uDCB8 lik qiymatga <b>TEKINGA</b> ega bo’ling \uD83E\uDD29❗\uFE0F\n" +
                                        "\n" +
                                        "<i>Davom etish uchun dastlab ushbu kanalga a’zo bo’ling</i> \uD83D\uDC47\n" +
                                        "\n" +
                                        "@articles365");
                                message.setParseMode("HTML");
                                message.setReplyMarkup(inlineKeyboardMarkup);

                                // Send the message
                                try {
                                    execute(message);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }

                                return;
                            }
                            case "/help" -> {
                                helpCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                                return;
                            }
                            default -> {
                                if (messageText.startsWith("/start")) {

                                    String startCommand = update.getMessage().getText();
                                    String sourceChatId = startCommand.substring(7); // Assuming the referral parameter is after '/start '
                                    Boolean isCreated =
                                            userFollowerService.create(
                                                    Long.valueOf(sourceChatId),
                                                    update.getMessage().getChatId()
                                            );

                                    if (isCreated) {
                                        usersService.createUser(
                                                update.getMessage().getChatId(),
                                                update.getMessage().getFrom().getFirstName(),
                                                update.getMessage().getFrom().getLastName()
                                        );

                                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                                        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                                        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                                        InlineKeyboardButton btn = new InlineKeyboardButton();
                                        btn.setText("Obuna bo'lish");
                                        btn.setUrl("https://t.me/testarticle365");
                                        rowInLine.add(btn);
                                        rows.add(rowInLine);

                                        rowInLine = new ArrayList<>();
                                        btn = new InlineKeyboardButton();
                                        btn.setText("Tekshirish ✅");
                                        btn.setCallbackData("/check");
                                        rowInLine.add(btn);
                                        rows.add(rowInLine);

                                        inlineKeyboardMarkup.setKeyboard(rows);

                                        SendMessage message = new SendMessage();
                                        message.setChatId(chatId);
                                        message.setText("Assalomu alaykum, Sizga link berilishi uchun quyidagi kanalga a'zo bo'ling.");
                                        message.setReplyMarkup(inlineKeyboardMarkup);

                                        // Send the message
                                        try {
                                            execute(message);
                                        } catch (TelegramApiException e) {
                                            e.printStackTrace();
                                        }

                                        return;
                                    }
                                }
                                sendMessage(chatId, "Sorry, command was not recognized");
                                return;
                            }
                        }
                    }
                    if (update.hasMessage() && update.getMessage().hasPhoto()) {

                    }
                }

            }
        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Long userId = update.getCallbackQuery().getMessage().getFrom().getId();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            if (data.equals("/check")) {
                if (isSubscribed("@testarticle365", chatId)) {
                    deleteMessageById(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());

                    System.out.println(userId);
                    Long fromId = userFollowerService.getFromId(chatId);

                    userFollowerService.setSubscribed(fromId, chatId);
                    System.out.println(fromId);

                    if (fromId != null) {
                        // 2
                        int count = 2 - (userFollowerService.getCount(fromId));

                        if (count == 0) {
                            sendMessage(fromId, "Malades!");
                        } else if (count > 0) {
                            sendMessage(fromId, "Bir dona obunachi qo'shildi! " + count + " dona qoldi!");
                        }
                    }

                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("<b>3 000 000</b> \uD83D\uDCB8lik qiymatga <b>TEKINGA</b> ega bo’ling❗\uFE0F\n" +
                            "\n" +
                            "<b>Assalomu alaykum, hurmatli foydalanuvchi !\n" +
                            "\n" +
                            "Yopiq kanalga :</b>\n" +
                            "\n" +
                            "<i>-kunlik <b>IELTS</b> formatdagi artikllar\n" +
                            "-kunlik <b>WORD QUIZ</b> lar\n" +
                            "-kunlik article bo’yicha <b>QUESTIONS</b>\n" +
                            "-kunlik questions ga <b>EXPLANATIONS</b> </i>\n" +
                            "\n" +
                            "<b>- AUTHENTIC MOCK</b>\uD83E\uDD29\n" +
                            "<b>- READING 9.0 MASTERCLASS</b> \uD83E\uDD29\n" +
                            "\n" +
                            "Shularni barchasiga <b>TEKINGA</b> ega bo’lish uchun bot sizga taqdim etgan referal linkni  bor yo’g’i 10 dona ingliz tili o’rganayotgan do’stingizga jo’natin !");
                    message.setParseMode("HTML");

                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                    List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                    InlineKeyboardButton btn = new InlineKeyboardButton();

                    rowInLine = new ArrayList<>();
                    btn = new InlineKeyboardButton();
                    btn.setText("Taklif posti✅");
                    btn.setCallbackData("/send");
                    rowInLine.add(btn);
                    rows.add(rowInLine);

                    inlineKeyboardMarkup.setKeyboard(rows);

                    message.setReplyMarkup(inlineKeyboardMarkup);

                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

//                    "Shu linkni 2 ta odamga tarqating \n" +
                }
            } else if (data.equals("/send")) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setPhoto(new InputFile(new java.io.File("wallpaper.jpg"), "wallpaper.jpg"));
                sendPhoto.setChatId(chatId);

                sendPhoto.setCaption("<b>3 000 000</b> \uD83D\uDCB8 lik qiymatga <b>TEKINGA</b> ega bo’ling !\n" +
                        "\n" +
                        "<i>-kunlik <b>IELTS</b> formatdagi artikllar\n" +
                        "-kunlik <b>WORD QUIZ</b> lar\n" +
                        "-kunlik article bo’yicha <b>QUESTIONS</b>\n" +
                        "-kunlik questions ga <b>EXPLANATIONS</b> \n" +
                        "</i>\n" +
                        "<b>- AUTHENTIC MOCK</b>\uD83E\uDD29\n" +
                        "<b>- READING 9.0 MASTERCLASS</b> \uD83E\uDD29\n" +
                        "\n");

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                InlineKeyboardButton btn = new InlineKeyboardButton();

                rowInLine = new ArrayList<>();
                btn = new InlineKeyboardButton();
                btn.setText("Tekinga olish");
                btn.setUrl("https://t.me/" + getBotUsername() + "?start=" + chatId);
                rowInLine.add(btn);
                rows.add(rowInLine);

                inlineKeyboardMarkup.setKeyboard(rows);

                sendPhoto.setParseMode("HTML");
                sendPhoto.setReplyMarkup(inlineKeyboardMarkup);

                try {
                    execute(sendPhoto);
                } catch (TelegramApiException e) {
                    System.out.println(e);
                }

                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("<b>Tepadagi ☝\uD83C\uDFFBpostni do'stlaringizga yuboring.</b>\n" +
                        "\n" +
                        "<b>10 ta</b> do'stingiz sizning taklif havolingiz orqali bot'ga kirib kanalga a'zo bo'lsa, bot sizga <b>yopiq kanal</b>\uD83D\uDE80 uchun bir martalik link beradi");
                message.setParseMode("HTML");

                try {
                    execute(message);
                } catch (TelegramApiException ignored) {}
            }
        }
    }

    private void helpCommandReceived(long chatId, String firstName) {
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText(textToSend);
        message.enableHtml(true);
        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }

    public boolean isSubscribed(String chatId, Long userId) {
        GetChatMember getChatMember = new GetChatMember(chatId, userId);
        try {
            ChatMember chatMember = execute(getChatMember);
            System.out.println(chatMember.getStatus());
            return chatMember.getStatus().equals("member") || chatMember.getStatus().equals("creator") || chatMember.getStatus().equals("administrator");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void deleteMessageById(Long chatId, Integer messageId) {
        try {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(messageId);

            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}