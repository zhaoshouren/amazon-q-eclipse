// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
<<<<<<< HEAD
import org.eclipse.core.runtime.Platform;
=======
>>>>>>> e87a173 (Fix open file/diff editor issues)

public final class MemoryStorage implements IStorage {
    private final String path;
    private final byte[] bytes;

    public MemoryStorage(final String path, final String body) {
        this.path = path;
        this.bytes = body.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public InputStream getContents() {
<<<<<<< HEAD
        return new ByteArrayInputStream(bytes.clone());
=======
        return new ByteArrayInputStream(bytes);
>>>>>>> e87a173 (Fix open file/diff editor issues)
    }

    @Override
    public IPath getFullPath() {
        return new Path(path);
    }

    @Override
    public String getName() {
<<<<<<< HEAD
        return path + " (preview)";
=======
        return getFullPath().lastSegment() + " (preview)";
>>>>>>> e87a173 (Fix open file/diff editor issues)
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
<<<<<<< HEAD
    public <T> T getAdapter(final Class<T> adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

=======
    @SuppressWarnings("rawtypes")
    public Object getAdapter(final Class c) {
        return null;
    }
>>>>>>> e87a173 (Fix open file/diff editor issues)
}
