// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package org.apache.commons.net.smtp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import org.apache.commons.net.io.CRLFLineReader;
import org.apache.commons.net.util.SSLContextUtils;

// Referenced classes of package org.apache.commons.net.smtp:
//            SMTPClient, SMTPReply

public class SMTPSClient extends SMTPClient
{

    private static final String DEFAULT_PROTOCOL = "TLS";
    private SSLContext context;
    private final boolean isImplicit;
    private KeyManager keyManager;
    private final String protocol;
    private String protocols[];
    private String suites[];
    private TrustManager trustManager;

    public SMTPSClient()
    {
        this("TLS", false);
    }

    public SMTPSClient(String s)
    {
        this(s, false);
    }

    public SMTPSClient(String s, boolean flag)
    {
        context = null;
        suites = null;
        protocols = null;
        trustManager = null;
        keyManager = null;
        protocol = s;
        isImplicit = flag;
    }

    public SMTPSClient(String s, boolean flag, String s1)
    {
        super(s1);
        context = null;
        suites = null;
        protocols = null;
        trustManager = null;
        keyManager = null;
        protocol = s;
        isImplicit = flag;
    }

    public SMTPSClient(SSLContext sslcontext)
    {
        this(false, sslcontext);
    }

    public SMTPSClient(boolean flag)
    {
        this("TLS", flag);
    }

    public SMTPSClient(boolean flag, SSLContext sslcontext)
    {
        context = null;
        suites = null;
        protocols = null;
        trustManager = null;
        keyManager = null;
        isImplicit = flag;
        context = sslcontext;
        protocol = "TLS";
    }

    private void initSSLContext()
        throws IOException
    {
        if (context == null)
        {
            context = SSLContextUtils.createSSLContext(protocol, getKeyManager(), getTrustManager());
        }
    }

    private void performSSLNegotiation()
        throws IOException
    {
        initSSLContext();
        Object obj = context.getSocketFactory();
        String s = getRemoteAddress().getHostAddress();
        int i = getRemotePort();
        obj = (SSLSocket)((SSLSocketFactory) (obj)).createSocket(_socket_, s, i, true);
        ((SSLSocket) (obj)).setEnableSessionCreation(true);
        ((SSLSocket) (obj)).setUseClientMode(true);
        if (protocols != null)
        {
            ((SSLSocket) (obj)).setEnabledProtocols(protocols);
        }
        if (suites != null)
        {
            ((SSLSocket) (obj)).setEnabledCipherSuites(suites);
        }
        ((SSLSocket) (obj)).startHandshake();
        _socket_ = ((java.net.Socket) (obj));
        _input_ = ((SSLSocket) (obj)).getInputStream();
        _output_ = ((SSLSocket) (obj)).getOutputStream();
        _reader = new CRLFLineReader(new InputStreamReader(_input_, encoding));
        _writer = new BufferedWriter(new OutputStreamWriter(_output_, encoding));
    }

    protected void _connectAction_()
        throws IOException
    {
        if (isImplicit)
        {
            performSSLNegotiation();
        }
        super._connectAction_();
    }

    public boolean execTLS()
        throws SSLException, IOException
    {
        if (!SMTPReply.isPositiveCompletion(sendCommand("STARTTLS")))
        {
            return false;
        } else
        {
            performSSLNegotiation();
            return true;
        }
    }

    public String[] getEnabledCipherSuites()
    {
        if (_socket_ instanceof SSLSocket)
        {
            return ((SSLSocket)_socket_).getEnabledCipherSuites();
        } else
        {
            return null;
        }
    }

    public String[] getEnabledProtocols()
    {
        if (_socket_ instanceof SSLSocket)
        {
            return ((SSLSocket)_socket_).getEnabledProtocols();
        } else
        {
            return null;
        }
    }

    public KeyManager getKeyManager()
    {
        return keyManager;
    }

    public TrustManager getTrustManager()
    {
        return trustManager;
    }

    public void setEnabledCipherSuites(String as[])
    {
        suites = new String[as.length];
        System.arraycopy(as, 0, suites, 0, as.length);
    }

    public void setEnabledProtocols(String as[])
    {
        protocols = new String[as.length];
        System.arraycopy(as, 0, protocols, 0, as.length);
    }

    public void setKeyManager(KeyManager keymanager)
    {
        keyManager = keymanager;
    }

    public void setTrustManager(TrustManager trustmanager)
    {
        trustManager = trustmanager;
    }
}