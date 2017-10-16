/*
 * Copyright 2015 Objectware Br
 * @author Luciano M. Christofoletti
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.objectware.commons.i18n;

import java.awt.Image;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import org.openide.util.*;
import org.openide.util.lookup.ServiceProvider;

/**
 * Classe responsável por fornecer acesso às strings de texto
 * internacionalizadas da aplicação.
 * 
 * Module dependencies: API de utilitários
 * 
 * Useful docs:
 *     http://docs.oracle.com/javase/tutorial/i18n/intro/index.html
 *     https://netbeans.org/kb/docs/java/gui-automatic-i18n.html
 *     http://bits.netbeans.org/dev/javadoc/org-openide-modules/org/openide/modules/doc-files/i18n-branding.html
 *     http://bits.netbeans.org/dev/javadoc/org-openide-util/org/openide/util/NbBundle.html
 *     https://blogs.oracle.com/geertjan/entry/runtime_swedish_english_language_switching
 *     http://netbeans.dzone.com/multilingual-netbeans-platform-applications
 * 
 * Service providers docs:
 *     http://developmentality.wordpress.com/2011/01/24/netbeans-platform-how-to-register-a-class-as-a-provider-of-multiple-services/
 *
 * To change the current locale: 
 * jvm options:
 *     -Duser.language=en
 *     -Duser.country=US
 */
@ServiceProvider(service = I18N.class, position = 10)
public final class I18N {
    
    /** Mecanismo de log da aplicação */
    //private static final Logger logger = Logger.getLogger(RRMSceneTopComponent.class.getName());
    
    /** I18N class resource bundle reference */
    private static final ResourceBundle MAIN_RESOURCE_BUNDLE = org.openide.util.NbBundle.getBundle(I18N.class);
    
    /** Date formatters (default and localized) */
    private static final String LOCALIZED_DATE_FORMATTER = MAIN_RESOURCE_BUNDLE.getString("localized.date.formatter");
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(LOCALIZED_DATE_FORMATTER);
    
    /** Date and time formatters (default and localized) */
    private static final String LOCALIZED_DATE_TIME_FORMATTER = MAIN_RESOURCE_BUNDLE.getString("localized.date.time.formatter");
    private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat(LOCALIZED_DATE_TIME_FORMATTER);
    
    /**
     * Main Resource Bundle access method
     * NOTE: this is not a singleton!!!
     * 
     * @return ResourceBundle
     */
    public static ResourceBundle getMainBundle() {
        if(I18N.MAIN_RESOURCE_BUNDLE == null) {
            return org.openide.util.NbBundle.getBundle(I18N.class); // TODO: this is a workaround. Must refator!
        }
        return I18N.MAIN_RESOURCE_BUNDLE;
    }   
    
    /**
     * Retorna uma string identificada pela chave "key" (localizada)
     *
     * @param key chave da string desejada
     * @return
     */
    public static String getString(String key) {
        try {
            return I18N.MAIN_RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException exception) {
            String missingKeyMessage = I18N.MAIN_RESOURCE_BUNDLE.getString("missing.key");
            return MessageFormat.format(missingKeyMessage, key);
        }   
    }   
    
    /**
     * String parametrizada
     *
     * @param key
     * @param params
     * @return
     */
    public static String getString(String key, Object... params) {
        return MessageFormat.format(I18N.getString(key), params);
    }
    
    /**
     * Retorna a imagem identificada pela chave "key"
     *
     * @param key
     * @return Image
     */
    public static Image getImage(String key) {
        return ImageUtilities.loadImage(I18N.getString(key), true);
    }
    
    /**
     * Retorna a imagem identificada pela chave "key"
     *
     * @param key
     * @param scale
     * @return Image
     */
    public static Image getScaledImage(String key, float scale) {
        
        Image image = ImageUtilities.loadImage(I18N.getString(key), true);
        int width = (int) (image.getWidth(null) * scale);
        int height = (int) (image.getHeight(null) * scale);
        
        return image.getScaledInstance(width, height, java.awt.Image.SCALE_FAST);
    }
    
    /**
     * Retorna a imagem do ícone identificado pela chave "key"
     *
     * @param key
     * @return ImageIcon
     */
    public static ImageIcon getImageIcon(String key) {
        return ImageUtilities.loadImageIcon(I18N.getString(key), true);
    }
    
    /**
     * Returns a formatted date string according to the current locale.
     *
     * @param time Timestamp
     * @return Date formatted string
     */
    public static String formatDate(Timestamp time) {
        return I18N.DATE_FORMATTER.format(time);
    }
    
    /**
     * Returns a formatted date/time string according to the current locale.
     *
     * @param time Timestamp
     * @return Date/Time formatted string
     */
    public static String formatDateTime(Timestamp time) {
        return I18N.DATE_TIME_FORMATTER.format(time);
    }
    
} // end of I18N class implementation
