package dev.iuif.sublang.forge;

import dev.iuif.sublang.config.SubLangConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class SubLangConfigGui extends GuiScreen {

    private final GuiScreen parent;
    private GuiButton enabledButton;
    private GuiButton sourceLanguageModeButton;
    private GuiTextField sourceLanguageField;
    private GuiTextField formatField;
    private GuiButton showForSameButton;

    private boolean enabled;
    private SubLangConfig.SourceLanguageMode sourceLanguageMode;
    private String sourceLanguage;
    private String format;
    private boolean showForSame;

    public SubLangConfigGui(GuiScreen parent) {
        this.parent = parent;
        SubLangConfig.ConfigData config = SubLangConfig.getConfig();
        this.enabled = config.enabled;
        this.sourceLanguageMode = config.sourceLanguageMode;
        this.sourceLanguage = config.sourceLanguage;
        this.format = config.format;
        this.showForSame = config.showForSameTranslation;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        int centerX = this.width / 2;
        int startY = 50;
        int spacing = 28;
        int buttonWidth = 200;

        // Enabled toggle
        this.enabledButton = new GuiButton(0, centerX - buttonWidth / 2, startY,
                buttonWidth, 20, getEnabledText());
        this.buttonList.add(enabledButton);

        // Source Language Mode toggle
        this.sourceLanguageModeButton = new GuiButton(1, centerX - buttonWidth / 2, startY + spacing,
                buttonWidth, 20, getSourceLanguageModeText());
        this.buttonList.add(sourceLanguageModeButton);

        // Source language field
        this.sourceLanguageField = new GuiTextField(2, this.fontRenderer,
                centerX - buttonWidth / 2, startY + spacing * 2, buttonWidth, 20);
        this.sourceLanguageField.setText(sourceLanguage);
        this.sourceLanguageField.setMaxStringLength(10);

        // Format field
        this.formatField = new GuiTextField(3, this.fontRenderer,
                centerX - buttonWidth / 2, startY + spacing * 3, buttonWidth, 20);
        this.formatField.setText(format);
        this.formatField.setMaxStringLength(50);

        // Show for same toggle
        this.showForSameButton = new GuiButton(4, centerX - buttonWidth / 2, startY + spacing * 4,
                buttonWidth, 20, getShowForSameText());
        this.buttonList.add(showForSameButton);

        // Done button
        this.buttonList.add(new GuiButton(5, centerX - buttonWidth / 2, this.height - 30,
                buttonWidth, 20, I18n.format("gui.done")));
    }

    private String getEnabledText() {
        return I18n.format("config.sublang.enabled") + ": " +
                (enabled ? I18n.format("options.on") : I18n.format("options.off"));
    }

    private String getSourceLanguageModeText() {
        String modeText = sourceLanguageMode == SubLangConfig.SourceLanguageMode.SPECIFIC_LANGUAGE
                ? I18n.format("config.sublang.sourceLanguageMode.specific")
                : I18n.format("config.sublang.sourceLanguageMode.key");
        return I18n.format("config.sublang.sourceLanguageMode") + ": " + modeText;
    }

    private String getShowForSameText() {
        return I18n.format("config.sublang.showForSame") + ": " +
                (showForSame ? I18n.format("options.on") : I18n.format("options.off"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: // Enabled toggle
                enabled = !enabled;
                enabledButton.displayString = getEnabledText();
                break;
            case 1: // Source Language Mode toggle
                sourceLanguageMode = sourceLanguageMode == SubLangConfig.SourceLanguageMode.SPECIFIC_LANGUAGE
                        ? SubLangConfig.SourceLanguageMode.TRANSLATION_KEY
                        : SubLangConfig.SourceLanguageMode.SPECIFIC_LANGUAGE;
                sourceLanguageModeButton.displayString = getSourceLanguageModeText();
                break;
            case 4: // Show for same toggle
                showForSame = !showForSame;
                showForSameButton.displayString = getShowForSameText();
                break;
            case 5: // Done
                saveConfig();
                this.mc.displayGuiScreen(parent);
                break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.sourceLanguageField.textboxKeyTyped(typedChar, keyCode);
        this.formatField.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_ESCAPE) {
            saveConfig();
            this.mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.sourceLanguageField.mouseClicked(mouseX, mouseY, mouseButton);
        this.formatField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format("config.sublang.title"),
                this.width / 2, 20, 0xFFFFFF);

        int centerX = this.width / 2;
        int startY = 50;
        int spacing = 28;
        int buttonWidth = 200;

        // Draw labels for text fields
        this.drawString(this.fontRenderer, I18n.format("config.sublang.sourceLanguage") + ":",
                centerX - buttonWidth / 2, startY + spacing * 2 - 12, 0xA0A0A0);
        this.drawString(this.fontRenderer, I18n.format("config.sublang.format") + ":",
                centerX - buttonWidth / 2, startY + spacing * 3 - 12, 0xA0A0A0);

        // Draw text fields
        this.sourceLanguageField.drawTextBox();
        this.formatField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.sourceLanguageField.updateCursorCounter();
        this.formatField.updateCursorCounter();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private void saveConfig() {
        SubLangConfig.ConfigData config = SubLangConfig.getConfig();
        config.enabled = this.enabled;
        config.sourceLanguageMode = this.sourceLanguageMode;
        config.sourceLanguage = this.sourceLanguageField.getText();
        config.format = this.formatField.getText();
        config.showForSameTranslation = this.showForSame;
        SubLangConfig.save();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
