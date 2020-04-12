/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era.pushnotifications;

import com.era.logger.LoggerUtility;
import com.era.pushernotifications.PusherPushNotificationsManager;
import com.era.pushernotifications.TrayIconManager;
import com.era.pushernotifications.data.PushDataModel;
import com.era.repositories.RepositoryFactory;
import com.era.repositories.models.HibernateConfigModel;
import com.era.repositories.utils.HibernateUtil;

/**
 *
 * @author PC
 */
public class main {
    
    public static void main(String[] args)
    {
        try{
            
            //Connect the connections params to hibernate
            HibernateConfigModel HibernateConfigModel = new HibernateConfigModel();
            HibernateConfigModel.setUser("root");
            HibernateConfigModel.setPassword("a5z8y1Tadeo");
            HibernateConfigModel.setPort(3306);
            HibernateConfigModel.setInstance("localhost");
            HibernateConfigModel.setDatabase("dbempresas");
            HibernateUtil.getSingleton().setHibernateConfigModel(HibernateConfigModel);
            
            final String channel = RepositoryFactory.getInstance().getLicenseRepository().getChannel();
            
            LoggerUtility.getSingleton().logInfo(PusherPushNotificationsManager.class, "Push Notifications: channel = " + channel);
            
            //Iinit pusher notifications
            final PusherPushNotificationsManager PusherPushNotificationsManager = new PusherPushNotificationsManager();
            PusherPushNotificationsManager.setPusherChannel(channel);
            PusherPushNotificationsManager.setIPushNotificationMessageSubscriber((PushDataModel PushDataModel) -> {

                try{

                    //Show the tray icon
                    final TrayIconManager TrayIconManager_ = TrayIconManager.getSingletlon();                
                    if (TrayIconManager_.isTraySupported()) {                                                            
                        TrayIconManager_.showPushNotification(PushDataModel);
                    } 
                    else {

                        LoggerUtility.getSingleton().logInfo(PusherPushNotificationsManager.class, "Push Notifications: System tray not supported!");
                    }

                    LoggerUtility.getSingleton().logInfo(PusherPushNotificationsManager.class, "Push Notifications: Saving in database the push notification");

                    //Save in the database the notification information
                    //RepositoryManager.getInstance().getPushNotificationsRepository().addLicense("", PushDataModel.getUrlBanner(), PushDataModel.getBannerAction());

                    LoggerUtility.getSingleton().logInfo(PusherPushNotificationsManager.class, "Push Notifications: Push notification saved into database");

                }catch(Exception e){
                    LoggerUtility.getSingleton().logError(main.class, e);                
                }
            });
            PusherPushNotificationsManager.connect();
            
            //Never ends this program
            Thread.currentThread().join();        
            
        }catch(Exception e){
            LoggerUtility.getSingleton().logError(main.class, e);
        }
    }
}
