// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.views;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class DialogContributionItem extends ContributionItem {
    private Dialog dialog;
    private String menuItemName;
    private Image icon;

    public DialogContributionItem(Dialog dialog, String menuItemName, Image icon) {
        this.dialog = dialog;
        this.menuItemName = menuItemName;
        this.icon = icon;
    }

    @Override
    public void fill(Menu menu, int index) {
        MenuItem menuItem = new MenuItem(menu, SWT.NONE, index);
        menuItem.setText(this.menuItemName);
        menuItem.setImage(this.icon);
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dialog.open();
            }
        });
    }
}