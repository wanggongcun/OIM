package com.time.oim.manager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import android.util.Log;


public class XmppConnectionManager {
	private static XMPPConnection connection = null;
	public static String serverdomain = "115.28.52.47";
	public static String serverhttpdomain = "115.28.52.47";
	public static String serverhttpname = "Administrator";
	public static int serverhttpport = 8888;
	public static String servername = "Administrator";
	public static int serverport = 5222;
	public static int fileserverport = 8080;
	public static String fileserverurl = "http://" + serverdomain + ":8080/OneServers/OneServlet";

	public static XmppConnectionManager xmppConnectionManager;
	public static VCard vCard = null;
	
//	断线重连,需要加载
	static{   
        try{  
           Class.forName("org.jivesoftware.smack.ReconnectionManager");  
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  
	
	private XmppConnectionManager() {

	}

	public static XmppConnectionManager getInstance() {
		if (xmppConnectionManager == null) {
			xmppConnectionManager = new XmppConnectionManager();
		}
		return xmppConnectionManager;
	}
//	public XMPPConnection init(LoginConfig loginConfig) {
//		Connection.DEBUG_ENABLED = false;
//		ProviderManager pm = ProviderManager.getInstance();
//		configureConnection(pm);
//
//		ConnectionConfiguration connectionConfig = new ConnectionConfiguration(serverdomain, serverport,servername);
//		connectionConfig.setSASLAuthenticationEnabled(false);// 不使用SASL验证，设置为false
//		connectionConfig
//				.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
//		// 允许自动连接
//		connectionConfig.setReconnectionAllowed(false);
//		// 允许登陆成功后更新在线状态
//		connectionConfig.setSendPresence(true);
//		// 收到好友邀请后manual表示需要经过同意,accept_all表示不经同意自动为好友
//		Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
//		connection = new XMPPConnection(connectionConfig);
//		try {
//			connection.connect();
//		} catch (XMPPException e) {
//			// TODO Auto-generated catch block
////			e.printStackTrace();
//			Log.d("cc","cc");
//		}
//		return connection;
//	}
	
	public XMPPConnection getConnection(){
		if(connection == null){
			try {
				connection = null;
				Connection.DEBUG_ENABLED = false;
				ProviderManager pm = ProviderManager.getInstance();
				configureConnection(pm);
				ConnectionConfiguration connectionConfig = new ConnectionConfiguration(serverdomain, serverport,servername);
				connectionConfig.setSASLAuthenticationEnabled(false);// 不使用SASL验证，设置为false
				connectionConfig
						.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
				// 允许自动连接
				connectionConfig.setReconnectionAllowed(true);
				// 允许登陆成功后更新在线状态
				connectionConfig.setSendPresence(true);
				connectionConfig.setCompressionEnabled(false);
				// 收到好友邀请后manual表示需要经过同意,accept_all表示不经同意自动为好友
				Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
				connection = new XMPPConnection(connectionConfig);
				
				connection.connect();
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				connection = null;
				return null;
			}
		}
		return connection;
	}
	
	private static void configureConnection(ProviderManager pm) {
		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",new PrivateDataManager.PrivateDataIQProvider());
		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",new RosterExchangeProvider());
		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",new MessageEventProvider());
		// Chat State
		pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",new XHTMLExtensionProvider());
		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",new GroupChatInvitation.Provider());
		// Service Discovery # Items //解析房间列表
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",new DiscoverItemsProvider());
		// Service Discovery # Info //某一个房间的信息
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",new DiscoverInfoProvider());
		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",new MUCUserProvider());
		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",new MUCAdminProvider());
		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",new MUCOwnerProvider());
		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",new DelayInformationProvider());
		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}
		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",new OfflineMessageRequest.Provider());
		// Offline Message Indicator
		pm.addExtensionProvider("offline","http://jabber.org/protocol/offline",new OfflineMessageInfo.Provider());
		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup",new SharedGroupsInfo.Provider());
		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses","http://jabber.org/protocol/address",new MultipleAddressesProvider());
		pm.addIQProvider("si", "http://jabber.org/protocol/si",new StreamInitiationProvider());
		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",new BytestreamsProvider());
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands",new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.SessionExpiredError());
	}
	
	
	public void changePassword(String pwd){
		if(connection == null)
			return;
		try {
			connection.getAccountManager().changePassword(pwd);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * 获取用户的vcard信息 .
	 * 
	 */
	public VCard getUserVCard(String jid) {
		XMPPConnection xmppConn = getConnection();
		VCard vcard = new VCard();
		try {
			vcard.load(xmppConn);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		
		return vcard;
	}

	/**
	 * 
	 * 保存用户的vcard信息. 注：修改vcard时，头像会丢失，此处为asmack.jar的bug，目前还无法修复
	 *
	 */
	public VCard saveUserVCard(VCard vCard) {
		XMPPConnection xmppConn = getConnection();
		try {
			vCard.save(xmppConn);
			return getUserVCard(vCard.getJabberId());
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 获取用户头像信息 .
	 * 
	 */
	public InputStream getUserImage(String jid) {
		XMPPConnection connection = getConnection();
		InputStream ic = null;
		try {
			System.out.println("获取用户头像信息: " + jid);
			VCard vcard = new VCard();
			vcard.load(connection, jid);

			if (vcard == null || vcard.getAvatar() == null) {
				return null;
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(
					vcard.getAvatar());
			return bais;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ic;
	}
}
