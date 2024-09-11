// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.views;

import java.io.IOException;
import java.net.URL;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import software.aws.toolkits.eclipse.amazonq.util.ClientMetadata;
import software.aws.toolkits.eclipse.amazonq.util.PluginLogger;
import software.aws.toolkits.eclipse.amazonq.util.PluginUtils;

public class FeedbackDialog extends Dialog {
    
    private static final String title = "Share Feedback";
    private static final int maxCharLimit = 2000;
    private Composite container;
    private Text commentBox;
    private Font magnifiedFont;
    private Image loadedImage;
    private Label characterRemainingLabel;
    private Sentiment selectedSentiment = Sentiment.POSITIVE;
    
    public class CustomRadioButton extends Composite {
        private Label iconLabel;
        private Label textLabel;
        private Button radioButton;

        public CustomRadioButton(Composite parent, Image image, String text, int style) {
            super(parent, style);
            
            Composite contentComposite = new Composite(parent, SWT.NONE);
            contentComposite.setLayout(new GridLayout(1, false));
            
            iconLabel = new Label(contentComposite, SWT.NONE);
            iconLabel.setImage(image);
            iconLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

            textLabel = new Label(contentComposite, SWT.NONE);
            textLabel.setText(text);
            textLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));

            radioButton = new Button(contentComposite, SWT.RADIO);
            radioButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
        }

        public Button getRadioButton() {
            return radioButton;
        }
    }
    
    enum Sentiment {
        POSITIVE,
        NEGATIVE
    }
    
    public FeedbackDialog(Shell parentShell) {
        super(parentShell);
    }
    
    private Image loadImage(String imagePath) {
        Image loadedImage = null;
        try {
            URL imageUrl = PluginUtils.getResource(imagePath);
            if (imageUrl != null) {
                // TODO: Need to add disposing logic for images
                loadedImage = new Image(Display.getCurrent(), imageUrl.openStream());
                this.loadedImage = loadedImage;
            }
        } catch (IOException e) {
            PluginLogger.warn(e.getMessage(), e);
        }
        return loadedImage;
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "Share", true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }
    
    @Override
    protected void okPressed() {
        Sentiment selectedSentiment = this.selectedSentiment;
        String comment = commentBox.getText();
        // TODO: to remove the info log post call to telemetry feedback api implementation
        PluginLogger.info(String.format("Selected sentiment: %s and comment: %s", selectedSentiment.toString(), comment));
        super.okPressed();
    }
    
    private Font magnifyFontSize(Font originalFont, int fontSize) {
        FontData[] fontData = originalFont.getFontData();
        for (int i = 0; i < fontData.length; i++) {
            fontData[i].setHeight(fontSize);
        }
        Font magnifiedFont = new Font(getShell().getDisplay(), fontData);
        // Dispose the previous magnifiedFont, if it exists
        if (this.magnifiedFont != null && !this.magnifiedFont.isDisposed()) {
            this.magnifiedFont.dispose();
        }
        this.magnifiedFont = magnifiedFont;
        return magnifiedFont;
    }
    
    private void handleTextModified(ModifyEvent event) {
        Text text = (Text) event.widget;
        if(text.getText().length() > maxCharLimit) {
            text.setText(text.getText().substring(0, maxCharLimit));
            text.setSelection(maxCharLimit); // Move the caret to the end of the text
        }
        updateCharacterRemainingCount();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));
       
        Composite headlineContainer = new Composite(container, SWT.NONE);
        RowLayout rowLayout = new RowLayout();
        rowLayout.spacing = 0; // to reduce space between two labels
        headlineContainer.setLayout(rowLayout);
        Label lookingForHelpLabel = new Label(headlineContainer, SWT.NONE);
        lookingForHelpLabel.setText("Looking for help? View the");
        lookingForHelpLabel.setFont(magnifyFontSize(lookingForHelpLabel.getFont(), 14));
        
        Label gettingStartedReferenceLabel = new Label(headlineContainer, SWT.NONE);
        gettingStartedReferenceLabel.setText("Getting Started Guide");
        gettingStartedReferenceLabel.setFont(magnifyFontSize(gettingStartedReferenceLabel.getFont(), 14));
        gettingStartedReferenceLabel.setForeground(headlineContainer.getDisplay().getSystemColor(SWT.COLOR_BLUE));
        gettingStartedReferenceLabel.setCursor(headlineContainer.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
        gettingStartedReferenceLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                PluginUtils.openWebpage("https://aws.amazon.com/q/getting-started/");
            }
        });
        
        Label orSearchOurTextLabel = new Label(headlineContainer, SWT.NONE);
        orSearchOurTextLabel.setText("or search our");
        orSearchOurTextLabel.setFont(magnifyFontSize(orSearchOurTextLabel.getFont(), 14));
        
        Label documentationReferenceLabel = new Label(headlineContainer, SWT.NONE);
        documentationReferenceLabel.setText("Documentation");
        documentationReferenceLabel.setFont(magnifyFontSize(documentationReferenceLabel.getFont(), 14));
        documentationReferenceLabel.setForeground(headlineContainer.getDisplay().getSystemColor(SWT.COLOR_BLUE));
        documentationReferenceLabel.setCursor(headlineContainer.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
        documentationReferenceLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                PluginUtils.openWebpage("https://docs.aws.amazon.com/amazonq/latest/qdeveloper-ug/what-is.html");
            }
        });
        
        Composite joinUsOnGithubContainer = new Composite(container, SWT.NONE);
        GridLayout joinUsOnGithubLayout = new GridLayout(2, false);
        joinUsOnGithubLayout.horizontalSpacing = 5;
        joinUsOnGithubContainer.setLayout(joinUsOnGithubLayout);
        joinUsOnGithubContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        Label joinUsGithubLabel = new Label(joinUsOnGithubContainer, SWT.NONE);
        joinUsGithubLabel.setText("Join us on GitHub");
        GridData joinGithubLayout = new GridData(SWT.FILL, SWT.CENTER, false, false);
        joinUsGithubLabel.setLayoutData(joinGithubLayout);
        joinUsGithubLabel.setFont(magnifyFontSize(joinUsGithubLabel.getFont(), 14));
        
        Label separatorLabel = new Label(joinUsOnGithubContainer, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData separatorGithubLayout = new GridData(SWT.FILL, SWT.CENTER, true, false);
        separatorLabel.setLayoutData(separatorGithubLayout);

        Composite reportRequestContributeContainer = new Composite(container, SWT.NONE);
        GridLayout reportRequestContributeContainerLayout = new GridLayout(2, false);
        reportRequestContributeContainerLayout.horizontalSpacing = 10;
        reportRequestContributeContainerLayout.marginLeft = 10;
        reportRequestContributeContainer.setLayout(reportRequestContributeContainerLayout);
        
        Label reportIssueImageLabel = new Label(reportRequestContributeContainer, SWT.NONE);
        reportIssueImageLabel.setImage(loadImage("icons/ReportAnIssue.png"));

        String bodyMessageForReportIssueOrRequestFeature = String.format(
        "--- \n"
        + "Toolkit: Amazon Q for %s\n"
        + "OS: %s %s\n"
        + "IDE: %s %s", ClientMetadata.getIdeName(), ClientMetadata.getOSName(),
        ClientMetadata.getOSVersion(), ClientMetadata.getIdeName(), 
        ClientMetadata.getIdeVersion()).stripIndent();
        
        Label reportIssue = new Label(reportRequestContributeContainer, SWT.NONE);
        reportIssue.setText("Report an issue");
        reportIssue.setForeground(reportRequestContributeContainer.getDisplay().getSystemColor(SWT.COLOR_BLUE));
        reportIssue.setCursor(reportRequestContributeContainer.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
        reportIssue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                PluginUtils.openWebpage(String.format("https://github.com/aws/amazon-q-eclipse/issues/new?body=%s", bodyMessageForReportIssueOrRequestFeature));
            }
        });
        
        Label requestFeatureImageLabel = new Label(reportRequestContributeContainer, SWT.NONE);
        requestFeatureImageLabel.setImage(loadImage("icons/RequestFeature.png"));
        
        Label requestFeature = new Label(reportRequestContributeContainer, SWT.NONE);
        requestFeature.setText("Request a feature");
        requestFeature.setForeground(reportRequestContributeContainer.getDisplay().getSystemColor(SWT.COLOR_BLUE));
        requestFeature.setCursor(reportRequestContributeContainer.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
        requestFeature.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                PluginUtils.openWebpage(String.format("https://github.com/aws/amazon-q-eclipse/issues/new?body=%s", bodyMessageForReportIssueOrRequestFeature));
            }
        });
        
        Label viewSourceCodeLabel = new Label(reportRequestContributeContainer, SWT.NONE);
        viewSourceCodeLabel.setImage(loadImage("icons/ViewCode.png"));
        
        Label viewSourceCode = new Label(reportRequestContributeContainer, SWT.NONE);
        viewSourceCode.setText("View source code and contribute");
        viewSourceCode.setForeground(reportRequestContributeContainer.getDisplay().getSystemColor(SWT.COLOR_BLUE));
        viewSourceCode.setCursor(reportRequestContributeContainer.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
        viewSourceCode.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                PluginUtils.openWebpage("https://github.com/aws/amazon-q-eclipse/");
            }
        });
        
        Composite shareFeedbackTextContainer = new Composite(container, SWT.NONE);
        GridLayout shareFeedbackTextContainerLayout = new GridLayout(2, false);
        shareFeedbackTextContainerLayout.horizontalSpacing = 5;
        shareFeedbackTextContainer.setLayout(shareFeedbackTextContainerLayout);
        shareFeedbackTextContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        Label shareFeedbackLabel = new Label(shareFeedbackTextContainer, SWT.NONE);
        shareFeedbackLabel.setText("Share feedback");
        shareFeedbackLabel.setFont(magnifyFontSize(shareFeedbackLabel.getFont(), 14));
        shareFeedbackLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        
        Label separatorForShareFeedbackLabel = new Label(shareFeedbackTextContainer, SWT.SEPARATOR | SWT.HORIZONTAL);
        separatorForShareFeedbackLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false)); 
        
        Composite questionsContainer = new Composite(container, SWT.NONE);
        GridLayout questionsContainerLayout = new GridLayout(1, false);
        questionsContainerLayout.marginLeft = 10;
        questionsContainer.setLayout(questionsContainerLayout);
        GridData questionsContainerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        questionsContainer.setLayoutData(questionsContainerLayoutData);
        
        Label ratingQuestionLabel = new Label(questionsContainer, SWT.NONE);
        ratingQuestionLabel.setText("How satisified are you with the AWS Toolkit?");
        
        Composite sentimentContainer = new Composite(questionsContainer, SWT.NONE);
        RowLayout sentimentContainerLayout = new RowLayout(SWT.HORIZONTAL);
        sentimentContainerLayout.spacing = 0;
        sentimentContainer.setLayout(sentimentContainerLayout);
        CustomRadioButton happySentimentButton = new CustomRadioButton(sentimentContainer, loadImage("icons/HappyFace.png"), "Satisfied", SWT.NONE);
        happySentimentButton.getRadioButton().setSelection(true);
        CustomRadioButton frownSentimentButton = new CustomRadioButton(sentimentContainer, loadImage("icons/FrownyFace.png"), "Unsatisfied", SWT.NONE);
        happySentimentButton.getRadioButton().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                frownSentimentButton.getRadioButton().setSelection(false);
                selectedSentiment = Sentiment.POSITIVE;
            }
        });
        frownSentimentButton.getRadioButton().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                happySentimentButton.getRadioButton().setSelection(false);
                selectedSentiment = Sentiment.NEGATIVE;
            }
        });
        
        Label feedbackQuestionLabel = new Label(questionsContainer, SWT.NONE);
        feedbackQuestionLabel.setText("What do you like about the AWS Toolkit? What can we improve?");
        
        
        commentBox = new Text(questionsContainer, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        GridData commentBoxLayout = new GridData(SWT.FILL, SWT.FILL, true, true);
        commentBoxLayout.heightHint = 200;
        commentBoxLayout.widthHint = 0;
        commentBox.setLayoutData(commentBoxLayout);
        commentBox.addModifyListener(this::handleTextModified);
        
        characterRemainingLabel = new Label(questionsContainer, SWT.NONE);
        characterRemainingLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        updateCharacterRemainingCount();
    
        return container;
    }
    
    private void updateCharacterRemainingCount() {
        int currentLength = commentBox.getText().length();
        int remainingCharacters = maxCharLimit - currentLength;
        characterRemainingLabel.setText(remainingCharacters + " characters remaining");
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(title);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(800, 600);
    }
    
    @Override
    public boolean close() {  
        disposeAllComponents(container);
        disposeIndependentElements();
        return super.close();
    }
    
    private void disposeAllComponents(Composite container) {
        for (Control control : container.getChildren()) {
            if (control instanceof Composite) {
                disposeAllComponents((Composite) control);
            } else {
                control.dispose();
            }
        }
    }
    
    public void disposeIndependentElements() {
        if (this.loadedImage != null && !this.loadedImage.isDisposed()) {
            this.loadedImage.dispose();
        }
        if (this.magnifiedFont != null && !this.magnifiedFont.isDisposed()) {
            this.magnifiedFont.dispose();
        }
    }
}