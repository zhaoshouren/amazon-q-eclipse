// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.editor;

import org.eclipse.core.resources.IStorage;
<<<<<<< HEAD
import org.eclipse.core.runtime.Platform;
=======
>>>>>>> e87a173 (Fix open file/diff editor issues)
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

public final class InMemoryInput implements IStorageEditorInput {
    private final IStorage storage;

    public InMemoryInput(final IStorage storage) {
        this.storage = storage;
    }

    @Override
    public IStorage getStorage() {
        return storage;
    }

    @Override
    public boolean exists() {
<<<<<<< HEAD
        return false;
=======
        return true;
>>>>>>> e87a173 (Fix open file/diff editor issues)
    }

    @Override
    public String getName() {
        return storage.getName();
    }

    @Override
    public String getToolTipText() {
        return getName();
    }

    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    @Override
<<<<<<< HEAD
    public ImageDescriptor getImageDescriptor() {
=======
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object getAdapter(final Class c) {
>>>>>>> e87a173 (Fix open file/diff editor issues)
        return null;
    }

    @Override
<<<<<<< HEAD
    public <T> T getAdapter(final Class<T> adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
=======
    public ImageDescriptor getImageDescriptor() {
        return null;
>>>>>>> e87a173 (Fix open file/diff editor issues)
    }
}
