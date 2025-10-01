// Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.eclipse.amazonq.lsp.connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import software.aws.toolkits.eclipse.amazonq.extensions.implementation.ActivatorStaticMockExtension;
import software.aws.toolkits.eclipse.amazonq.extensions.implementation.DefaultLspEncryptionManagerStaticMockExtension;
import software.aws.toolkits.eclipse.amazonq.extensions.implementation.LspManagerProviderStaticMockExtension;
import software.aws.toolkits.eclipse.amazonq.extensions.implementation.ProxyUtilsStaticMockExtension;
import software.aws.toolkits.eclipse.amazonq.lsp.encryption.LspEncryptionManager;
import software.aws.toolkits.eclipse.amazonq.lsp.manager.LspInstallResult;
import software.aws.toolkits.eclipse.amazonq.plugin.Activator;
import software.aws.toolkits.eclipse.amazonq.util.LoggingService;
import software.aws.toolkits.eclipse.amazonq.util.PluginPlatform;
import software.aws.toolkits.eclipse.amazonq.util.PluginUtils;
import software.aws.toolkits.eclipse.amazonq.util.ProxyUtil;
import software.aws.toolkits.eclipse.amazonq.preferences.AmazonQPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;

public final class QLspConnectionProviderTest {

    @TempDir
    private java.nio.file.Path tempDir;

    @RegisterExtension
    private static ActivatorStaticMockExtension activatorStaticMockExtension = new ActivatorStaticMockExtension();

    @RegisterExtension
    private static LspManagerProviderStaticMockExtension lspManagerProviderStaticMockExtension
            = new LspManagerProviderStaticMockExtension();

    @RegisterExtension
    private static DefaultLspEncryptionManagerStaticMockExtension lspEncryptionManagerStaticMockExtension
            = new DefaultLspEncryptionManagerStaticMockExtension();

    @RegisterExtension
    private static ProxyUtilsStaticMockExtension proxyUtilsStaticMockExtension = new ProxyUtilsStaticMockExtension();

    private MockedStatic<PluginUtils> pluginUtilsMock;
    private IPreferenceStore preferenceStore;

    private static final class TestProcessConnectionProvider extends ProcessStreamConnectionProvider {

        TestProcessConnectionProvider(final List<String> commands, final String workingDirectory) {
            super(commands, workingDirectory);
        }

    }

    private static final class TestQLspConnectionProvider extends QLspConnectionProvider {

        TestQLspConnectionProvider() throws IOException {
            super();
        }

        public void testAddEnvironmentVariables(final Map<String, String> env) {
            super.addEnvironmentVariables(env);
        }

    }

    @BeforeEach
    void setupMocks() {
        pluginUtilsMock = Mockito.mockStatic(PluginUtils.class);
        pluginUtilsMock.when(PluginUtils::getPlatform).thenReturn(PluginPlatform.LINUX);

        preferenceStore = Mockito.mock(IPreferenceStore.class);
        var activatorMock = activatorStaticMockExtension.getMock(Activator.class);
        Mockito.when(activatorMock.getPreferenceStore()).thenReturn(preferenceStore);
    }

    @AfterEach
    void tearDownMocks() {
        if (pluginUtilsMock != null) {
            pluginUtilsMock.close();
        }
    }

    @Test
    void testConstructorInitializesCorrectly() throws IOException {
        String testDir = tempDir.toString();
        String serverPath = Paths.get(testDir, "server.js").toString();

        LspInstallResult lspInstallResultMock = lspManagerProviderStaticMockExtension.getMock(LspInstallResult.class);
        Mockito.when(lspInstallResultMock.getServerDirectory()).thenReturn(testDir);
        Mockito.when(lspInstallResultMock.getServerCommand()).thenReturn("server.js");
        Mockito.when(lspInstallResultMock.getServerCommandArgs()).thenReturn("--test-arg");

        var provider = new QLspConnectionProvider();

        List<String> expectedCommands = List.of(
                serverPath,
                "--test-arg",
                "--stdio",
                "--set-credentials-encryption-key"
        );

        TestProcessConnectionProvider testProcessConnectionProvider = new TestProcessConnectionProvider(
                expectedCommands,
                testDir
        );

        assertTrue(testProcessConnectionProvider.equals(provider));
    }

    @Test
    void testAddEnvironmentVariablesWithoutProxy() throws IOException {
        String testDir = tempDir.toString();

        LspInstallResult lspInstallResultMock = lspManagerProviderStaticMockExtension.getMock(LspInstallResult.class);
        Mockito.when(lspInstallResultMock.getServerDirectory()).thenReturn(testDir);
        Mockito.when(lspInstallResultMock.getServerCommand()).thenReturn("server.js");
        Mockito.when(lspInstallResultMock.getServerCommandArgs()).thenReturn("");

        MockedStatic<ProxyUtil> proxyUtilStaticMock = proxyUtilsStaticMockExtension.getStaticMock();
        proxyUtilStaticMock.when(ProxyUtil::getHttpsProxyUrl).thenReturn("");

        Map<String, String> env = new HashMap<>();

        var provider = new TestQLspConnectionProvider();
        provider.testAddEnvironmentVariables(env);

        assertEquals("true", env.get("ENABLE_INLINE_COMPLETION"));
        assertEquals("true", env.get("ENABLE_TOKEN_PROVIDER"));
        assertFalse(env.containsKey("HTTPS_PROXY"));
    }

    @Test
    void testAddEnvironmentVariablesWithProxy() throws IOException {
        String testDir = tempDir.toString();

        LspInstallResult lspInstallResultMock = lspManagerProviderStaticMockExtension.getMock(LspInstallResult.class);
        Mockito.when(lspInstallResultMock.getServerDirectory()).thenReturn(testDir);
        Mockito.when(lspInstallResultMock.getServerCommand()).thenReturn("server.js");
        Mockito.when(lspInstallResultMock.getServerCommandArgs()).thenReturn("");

        MockedStatic<ProxyUtil> proxyUtilStaticMock = proxyUtilsStaticMockExtension.getStaticMock();
        proxyUtilStaticMock.when(ProxyUtil::getHttpsProxyUrl).thenReturn("http://proxy:8080");

        Map<String, String> env = new HashMap<>();
        env.put("HTTPS_PROXY", "http://proxy:8080");

        var provider = new TestQLspConnectionProvider();
        provider.testAddEnvironmentVariables(env);

        assertEquals("true", env.get("ENABLE_INLINE_COMPLETION"));
        assertEquals("true", env.get("ENABLE_TOKEN_PROVIDER"));
        assertEquals("http://proxy:8080", env.get("HTTPS_PROXY"));
    }

    @Test
    void testStartInitializesEncryptedCommunication() throws IOException {
        String testDir = tempDir.toString();

        LspInstallResult lspInstallResultMock = lspManagerProviderStaticMockExtension.getMock(LspInstallResult.class);
        Mockito.when(lspInstallResultMock.getServerDirectory()).thenReturn(testDir);
        Mockito.when(lspInstallResultMock.getServerCommand()).thenReturn("server.js");
        Mockito.when(lspInstallResultMock.getServerCommandArgs()).thenReturn("");

        var provider = Mockito.spy(new QLspConnectionProvider());
        doNothing().when(provider).startProcess();

        var mockOutputStream = Mockito.mock(OutputStream.class);
        doReturn(mockOutputStream).when(provider).getOutputStream();

        provider.start();

        LoggingService loggerMock = activatorStaticMockExtension.getMock(LoggingService.class);
        verify(loggerMock).info("Initializing communication with Amazon Q Lsp Server");

        LspEncryptionManager lspEncryptionManagerMock = lspEncryptionManagerStaticMockExtension.getMock(
                LspEncryptionManager.class
        );
        verify(lspEncryptionManagerMock).initializeEncryptedCommunication(mockOutputStream);
    }

    @Test
    void testStartLogsErrorOnException() throws IOException {
        String testDir = tempDir.toString();

        LspInstallResult lspInstallResultMock = lspManagerProviderStaticMockExtension.getMock(LspInstallResult.class);
        Mockito.when(lspInstallResultMock.getServerDirectory()).thenReturn(testDir);
        Mockito.when(lspInstallResultMock.getServerCommand()).thenReturn("server.js");
        Mockito.when(lspInstallResultMock.getServerCommandArgs()).thenReturn("");

        var provider = Mockito.spy(new QLspConnectionProvider());
        doNothing().when(provider).startProcess();

        var mockOutputStream = Mockito.mock(OutputStream.class);
        doReturn(mockOutputStream).when(provider).getOutputStream();

        RuntimeException testException = new RuntimeException("Test error");
        LspEncryptionManager lspEncryptionManagerMock = lspEncryptionManagerStaticMockExtension.getMock(
                LspEncryptionManager.class
        );

        doThrow(testException).when(lspEncryptionManagerMock)
                .initializeEncryptedCommunication(any());

        provider.start();

        LoggingService loggingServiceMock = activatorStaticMockExtension.getMock(LoggingService.class);
        verify(loggingServiceMock).error("Error occured while initializing communication with Amazon Q Lsp Server",
                testException);
    }

    @Test
    void testCertInjectionWithUserPreference() throws IOException {
        String testDir = tempDir.toString();

        LspInstallResult lspInstallResultMock = lspManagerProviderStaticMockExtension.getMock(LspInstallResult.class);
        Mockito.when(lspInstallResultMock.getServerDirectory()).thenReturn(testDir);
        Mockito.when(lspInstallResultMock.getServerCommand()).thenReturn("server.js");
        Mockito.when(lspInstallResultMock.getServerCommandArgs()).thenReturn("");

        Mockito.when(preferenceStore.getString(AmazonQPreferencePage.CA_CERT)).thenReturn("/path/to/user/cert.pem");

        MockedStatic<ProxyUtil> proxyUtilStaticMock = proxyUtilsStaticMockExtension.getStaticMock();
        proxyUtilStaticMock.when(ProxyUtil::getHttpsProxyUrl).thenReturn("");

        Map<String, String> env = new HashMap<>();
        var provider = new TestQLspConnectionProvider();
        provider.testAddEnvironmentVariables(env);

        assertEquals("/path/to/user/cert.pem", env.get("NODE_EXTRA_CA_CERTS"));
        assertEquals("/path/to/user/cert.pem", env.get("AWS_CA_BUNDLE"));
    }

    @Test
    void testNoCertInjectionWhenNoCertsFound() throws IOException {
        String testDir = tempDir.toString();

        LspInstallResult lspInstallResultMock = lspManagerProviderStaticMockExtension.getMock(LspInstallResult.class);
        Mockito.when(lspInstallResultMock.getServerDirectory()).thenReturn(testDir);
        Mockito.when(lspInstallResultMock.getServerCommand()).thenReturn("server.js");
        Mockito.when(lspInstallResultMock.getServerCommandArgs()).thenReturn("");

        Mockito.when(preferenceStore.getString(AmazonQPreferencePage.CA_CERT)).thenReturn("");

        MockedStatic<ProxyUtil> proxyUtilStaticMock = proxyUtilsStaticMockExtension.getStaticMock();
        proxyUtilStaticMock.when(ProxyUtil::getHttpsProxyUrl).thenReturn("");
        proxyUtilStaticMock.when(ProxyUtil::getCertificatesAsPem).thenReturn(null);

        Map<String, String> env = new HashMap<>();
        var provider = new TestQLspConnectionProvider();
        provider.testAddEnvironmentVariables(env);

        assertFalse(env.containsKey("NODE_EXTRA_CA_CERTS"));
        assertFalse(env.containsKey("AWS_CA_BUNDLE"));
    }

}
