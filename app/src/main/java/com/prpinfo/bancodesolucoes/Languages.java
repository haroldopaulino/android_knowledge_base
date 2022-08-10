package com.prpinfo.bancodesolucoes;

import org.json.JSONException;
import org.json.JSONObject;

public class Languages {

    private JSONObject
            englishObject,
            frenchObject,
            spanishObject,
            portuguesObject;

    Languages() {
        _loadValues();
    }

    public String getText(String language, String item) {
        String output = "";
        try {
            switch(language.toUpperCase().trim()) {
                case "" :
                case "0" :
                case "ENGLISH" :
                    output = englishObject.getString(item);
                    break;
                case "1" :
                case "SPANISH" :
                    output = spanishObject.getString(item);
                    break;
                case "2" :
                case "FRENCH" :
                    output = frenchObject.getString(item);
                    break;
                case "3" :
                case "PORTUGUESE" :
                    output = portuguesObject.getString(item);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    private void _loadValues() {
        _loadEnglishValues();
        _loadFrenchValues();
        _loadSpanishValues();
        _loadPortugueseValues();
    }

    private void _loadEnglishValues() {
        try {
            englishObject = new JSONObject();
            englishObject.put("QUOTE_FRAGMENT_TITLE", "Quote Of The Day");
            englishObject.put("SHARE_FRAGMENT_TITLE", "Share Today's Quote");
            englishObject.put("SETTINGS_FRAGMENT_TITLE", "Settings");
            englishObject.put("SHARE_FRAGMENT_GROUPS_SETTING_TITLE", "My Groups");
            englishObject.put("SHARE_FRAGMENT_SHARE_WITH_GROUPS_BUTTON", "SHARE WITH GROUPS");
            englishObject.put("SHARE_FRAGMENT_SHARE_THROUGH_ANOTHER_APP_BUTTON", "SHARE THROUGH ANOTHER APP");
            englishObject.put("NAVIGATION_QUOTE_TITLE", "Quotes");
            englishObject.put("NAVIGATION_SHARE_TITLE", "Share");
            englishObject.put("NAVIGATION_SETTINGS_TITLE", "Settings");
            englishObject.put("SETTINGS_FRAGMENT_LANGUAGE_TITLE", "Language");
            englishObject.put("SETTINGS_FRAGMENT_NOTIFICATION_TITLE", "Notification");
            englishObject.put("SETTINGS_FRAGMENT_ALERT_TITLE", "Alert");
            englishObject.put("SETTINGS_FRAGMENT_FREQUENCY_TITLE", "Frequency");
            englishObject.put("SETTINGS_FRAGMENT_TIME_TITLE", "Time");
            englishObject.put("ALERT_GROUPS_MANAGEMENT_MY_GROUPS_TITLE", "My Groups");
            englishObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_A_GROUP_BUTTON", "CREATE A GROUP");
            englishObject.put("ALERT_GROUPS_MANAGEMENT_DELETE_CONFIRMATION_TITLE", "Confirm Delete");
            englishObject.put("ALERT_GROUPS_MANAGEMENT_DELETE_CONFIRMATION_MESSAGE", "Delete PLACEHOLDER?");
            englishObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_MAIN_TITLE", "Create Group");
            englishObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_FIELD_TITLE", "Group Name");
            englishObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_DONE_BUTTON", "CREATE");
            englishObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_BLANK_MESSAGE", "Group cannot be blank!");
            englishObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_ALREADY_EXISTS_MESSAGE", "The Group already exists!");
            englishObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_SUCCESSFUL_CREATION_MESSAGE", "Group created successfully!");
            englishObject.put("GROUP_MEMBER_COUNT_MESSAGE_HAS_NO_MEMBERS", "has no members");
            englishObject.put("GROUP_MEMBER_COUNT_MESSAGE_HAS_1_MEMBER", "has 1 member");
            englishObject.put("GROUP_MEMBER_COUNT_MESSAGE_HAS_MULTIPLE_MEMBERS", "has PLACEHOLDER members");
            englishObject.put("YES", "YES");
            englishObject.put("NO", "NO");
            englishObject.put("DONE", "DONE");
            englishObject.put("GROUP", "Group");
            englishObject.put("CONTACTS", "Contacts");
            englishObject.put("LOADING", "Loading ...");
            englishObject.put("SHARE_MESSAGE_PART_AUTHOR", "The quote is from the Author");
            englishObject.put("SHARE_MESSAGE_PART_MESSAGE", "Message from the 'Papi Quotes' Android app!");
            englishObject.put("SHARE_MESSAGE_PART_VIA", "Share the Quote via");
            englishObject.put("SHARE_MESSAGE_PART_RECEIVING", "You are receiving a message from the 'Papi Quotes' Android app!");
            englishObject.put("ALERT_GROUPS_MANAGEMENT_ADD_MEMBERS_BUTTON", "ADD MEMBERS");
            englishObject.put("ABOUT", "About");
            englishObject.put("PORTFOLIO_FILENAME", "portfolio_english.html");
            englishObject.put("VIEW", "VIEW");
            englishObject.put("VIBRATE", "Vibrate");
            englishObject.put("SOUND", "Sound");
            englishObject.put("DAILY", "Daily");
            englishObject.put("WEEKLY", "Weekly");
            englishObject.put("PRIVACY_POLICY", "Privacy Policy");
            englishObject.put("PRIVACY_POLICY_CONTENTS",
                    "Haroldo Paulino built the Papi Quotes app as a Free app. This SERVICE is provided by Haroldo Paulino at no cost and is intended for use as is.\n" +
                            "\n" +
                            "This policy is used to inform App visitors regarding my policies with the collection, use, and disclosure of Personal Information if anyone decided to use my Service.\n" +
                            "\n" +
                            "If you choose to use my Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that I collect is used for providing and improving the Service. I will not use or share your information with anyone except as described in this Privacy Policy.\n" +
                            "\n" +
                            "The terms used in this Privacy Policy have the same meanings as in our Terms and Conditions, which is accessible at Papi Quotes unless otherwise defined in this Privacy Policy.\n" +
                            "\n" +
                            "Information Collection and Use\n" +
                            "\n" +
                            "For a better experience, while using our Service, I may require you to provide us with certain personally identifiable information, including but not limited to Access to the Device State and Contacts. The information that I request is retained on your device and is not collected by me in any way\n" +
                            "\n" +
                            "The app does use third party services that may collect information used to identify you.\n" +
                            "\n" +
                            "Link to privacy policy of third party service providers used by the app\n" +
                            "\n" +
                            "Google Play Services\n" +
                            "Log Data\n" +
                            "\n" +
                            "I want to inform you that whenever you use my Service, in a case of an error in the app I collect data and information (through third party products) on your phone called Log Data. This Log Data may include information such as your device Internet Protocol (?IP?) address, device name, operating system version, the configuration of the app when utilizing my Service, the time and date of your use of the Service, and other statistics.\n" +
                            "\n" +
                            "Service Providers\n" +
                            "\n" +
                            "I may employ third-party companies and individuals due to the following reasons:\n" +
                            "\n" +
                            "* To facilitate our Service;\n" +
                            "* To provide the Service on our behalf;\n" +
                            "* To perform Service-related services; or\n" +
                            "* To assist us in analyzing how our Service is used.\n" +
                            "* I want to inform users of this Service that these third parties have access to your Personal Information. The reason is to perform the tasks assigned to them on our behalf. However, they are obligated not to disclose or use the information for any other purpose.\n" +
                            "\n" +
                            "Security\n" +
                            "\n" +
                            "I value your trust in providing us your Personal Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and I cannot guarantee its absolute security.\n" +
                            "\n" +
                            "Links to Other Sites\n" +
                            "\n" +
                            "This Service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by me. Therefore, I strongly advise you to review the Privacy Policy of these websites. I have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.\n" +
                            "\n" +
                            "Children?s Privacy\n" +
                            "\n" +
                            "These Services do not address anyone under the age of 13. I do not knowingly collect personally identifiable information from children under 13. In the case I discover that a child under 13 has provided me with personal information, I immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact me so that I will be able to do necessary actions.\n" +
                            "\n" +
                            "Changes to This Privacy Policy\n" +
                            "\n" +
                            "I may update our Privacy Policy from time to time. Thus, you are advised to review this page periodically for any changes. I will notify you of any changes by posting the new Privacy Policy on this page. These changes are effective immediately after they are posted on this page.\n" +
                            "\n" +
                            "Contact Us\n" +
                            "\n" +
                            "If you have any questions or suggestions about my Privacy Policy, do not hesitate to contact me.");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void _loadSpanishValues() {
        try {
            spanishObject = new JSONObject();
            spanishObject.put("QUOTE_FRAGMENT_TITLE", "Cita del día");
            spanishObject.put("SHARE_FRAGMENT_TITLE", "Comparte la cita de hoy");
            spanishObject.put("SETTINGS_FRAGMENT_TITLE", "Configuraciones");
            spanishObject.put("SHARE_FRAGMENT_GROUPS_SETTING_TITLE", "Mis Grupos");
            spanishObject.put("SHARE_FRAGMENT_SHARE_WITH_GROUPS_BUTTON", "COMPARTIR CON GRUPOS");
            spanishObject.put("SHARE_FRAGMENT_SHARE_THROUGH_ANOTHER_APP_BUTTON", "COMPARTIR POR OTRA APP");
            spanishObject.put("NAVIGATION_QUOTE_TITLE", "Citas");
            spanishObject.put("NAVIGATION_SHARE_TITLE", "Compartir");
            spanishObject.put("NAVIGATION_SETTINGS_TITLE", "Configuraciones");
            spanishObject.put("SETTINGS_FRAGMENT_LANGUAGE_TITLE", "Idioma");
            spanishObject.put("SETTINGS_FRAGMENT_NOTIFICATION_TITLE", "Notificación");
            spanishObject.put("SETTINGS_FRAGMENT_ALERT_TITLE", "Alerta");
            spanishObject.put("SETTINGS_FRAGMENT_FREQUENCY_TITLE", "Frecuencia");
            spanishObject.put("SETTINGS_FRAGMENT_TIME_TITLE", "Hora");
            spanishObject.put("ALERT_GROUPS_MANAGEMENT_MY_GROUPS_TITLE", "Mis Grupos");
            spanishObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_A_GROUP_BUTTON", "CREA UN GRUPO");
            spanishObject.put("ALERT_GROUPS_MANAGEMENT_DELETE_CONFIRMATION_TITLE", "Confirmar eliminación");
            spanishObject.put("ALERT_GROUPS_MANAGEMENT_DELETE_CONFIRMATION_MESSAGE", "Eliminar PLACEHOLDER?");
            spanishObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_MAIN_TITLE", "Crea un Grupo");
            spanishObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_FIELD_TITLE", "Nombre del Grupo");
            spanishObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_DONE_BUTTON", "CREAR");
            spanishObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_BLANK_MESSAGE", "El Grupo no puede estar en blanco!");
            spanishObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_ALREADY_EXISTS_MESSAGE", "El Grupo ya existe!");
            spanishObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_SUCCESSFUL_CREATION_MESSAGE", "Grupo creado con éxito!");
            spanishObject.put("GROUP_MEMBER_COUNT_MESSAGE_HAS_NO_MEMBERS", "no tiene miembros");
            spanishObject.put("GROUP_MEMBER_COUNT_MESSAGE_HAS_1_MEMBER", "tiene 1 miembro");
            spanishObject.put("GROUP_MEMBER_COUNT_MESSAGE_HAS_MULTIPLE_MEMBERS", "tiene PLACEHOLDER miembros");
            spanishObject.put("YES", "SÍ");
            spanishObject.put("NO", "NO");
            spanishObject.put("DONE", "HECHO");
            spanishObject.put("GROUP", "Grupo");
            spanishObject.put("CONTACTS", "Contactos");
            spanishObject.put("LOADING", "Cargando ...");
            spanishObject.put("SHARE_MESSAGE_PART_AUTHOR", "La cita es del autor");
            spanishObject.put("SHARE_MESSAGE_PART_MESSAGE", "Mensaje de la aplicación para Android 'Papi Quotes'");
            spanishObject.put("SHARE_MESSAGE_PART_VIA", "Comparte el mensaje a través de");
            spanishObject.put("SHARE_MESSAGE_PART_RECEIVING", "¡Estás recibiendo un mensaje de la aplicación para Android 'Papi Quotes'!");
            spanishObject.put("ALERT_GROUPS_MANAGEMENT_ADD_MEMBERS_BUTTON", "AÑADIR MIEMBROS");
            spanishObject.put("ABOUT", "Sobre");
            spanishObject.put("PORTFOLIO_FILENAME", "portfolio_spanish.html");
            spanishObject.put("VIEW", "VER");
            spanishObject.put("VIBRATE", "Vibrar");
            spanishObject.put("SOUND", "Sonar");
            spanishObject.put("DAILY", "Diario");
            spanishObject.put("WEEKLY", "Semanal");
            spanishObject.put("PRIVACY_POLICY", "Política de Privacidad");
            spanishObject.put("PRIVACY_POLICY_CONTENTS",
                    "Haroldo Paulino creó la aplicación Papi Quotes como una aplicación gratuita. Este SERVICIO es provisto por Haroldo Paulino sin costo y está destinado a ser utilizado tal cual.\n" +
                            "\n" +
                            "Esta política se utiliza para informar a los visitantes de la Aplicación sobre mis políticas con la recopilación, el uso y la divulgación de Información personal si alguien decide utilizar mi Servicio.\n" +
                            "\n" +
                            "Si elige usar mi Servicio, acepta la recopilación y el uso de la información en relación con esta política. La información personal que recopilo se utiliza para proporcionar y mejorar el servicio. No utilizaré ni compartiré su información con nadie, excepto como se describe en esta Política de privacidad.\n" +
                            "\n" +
                            "Los términos utilizados en esta Política de privacidad tienen los mismos significados que en nuestros Términos y condiciones, a los que se puede acceder en las cotizaciones de Papi, a menos que se establezca lo contrario en esta Política de privacidad.\n" +
                            "\n" +
                            "Recopilación y uso de información\n" +
                            "\n" +
                            "Para una mejor experiencia, al utilizar nuestro Servicio, es posible que le pidamos que nos brinde cierta información de identificación personal, que incluye, pero no se limita a, Acceso al Estado del dispositivo y Contactos. La información que solicito se conserva en su dispositivo y no la recopilo de ninguna manera\n" +
                            "\n" +
                            "La aplicación utiliza servicios de terceros que pueden recopilar información utilizada para identificarlo.\n" +
                            "\n" +
                            "Enlace a la política de privacidad de proveedores de servicios de terceros utilizados por la aplicación\n" +
                            "\n" +
                            "Servicios de Google Play\n" +
                            "Dato de registro\n" +
                            "\n" +
                            "Deseo informarle que cada vez que utiliza mi Servicio, en caso de error en la aplicación, recopilo datos e información (a través de productos de terceros) en su teléfono llamados Datos de registro. Este registro de datos puede incluir información como la dirección del protocolo de Internet (\"IP\") del dispositivo, el nombre del dispositivo, la versión del sistema operativo, la configuración de la aplicación al utilizar mi servicio, la hora y fecha de uso del servicio y otras estadísticas .\n" +
                            "\n" +
                            "Proveedores de servicio\n" +
                            "\n" +
                            "Puedo emplear compañías e individuos de terceros debido a las siguientes razones:\n" +
                            "\n" +
                            "* Para facilitar nuestro Servicio;\n" +
                            "* Para proporcionar el Servicio en nuestro nombre;\n" +
                            "* Para realizar servicios relacionados con el servicio; o\n" +
                            "* Para ayudarnos a analizar cómo se utiliza nuestro Servicio.\n" +
                            "* Deseo informar a los usuarios de este Servicio que estos terceros tienen acceso a su Información personal. El motivo es realizar las tareas que se les asignaron en nuestro nombre. Sin embargo, están obligados a no divulgar ni utilizar la información para ningún otro fin.\n" +
                            "\n" +
                            "Seguridad\n" +
                            "\n" +
                            "Valoro su confianza al proporcionarnos su Información personal, por lo tanto, nos esforzamos por utilizar medios comercialmente aceptables para protegerla. Pero recuerde que ningún método de transmisión a través de Internet o método de almacenamiento electrónico es 100% seguro y confiable, y no puedo garantizar su absoluta seguridad.\n" +
                            "\n" +
                            "Enlaces a otros sitios\n" +
                            "\n" +
                            "Este Servicio puede contener enlaces a otros sitios. Si hace clic en un enlace de un tercero, se lo dirigirá a ese sitio. Tenga en cuenta que estos sitios externos no son operados por mí. Por lo tanto, le recomiendo encarecidamente que revise la Política de privacidad de estos sitios web. No tengo control ni asumo ninguna responsabilidad por el contenido, las políticas de privacidad o las prácticas de sitios o servicios de terceros.\n" +
                            "\n" +
                            "Privacidad de los niños\n" +
                            "\n" +
                            "Estos Servicios no se dirigen a personas menores de 13 años. No recopilé a sabiendas información personal identificable de niños menores de 13 años. En caso de que descubra que un niño menor de 13 años me ha proporcionado información personal, inmediatamente la borro de nuestros servidores. Si usted es un padre o tutor y sabe que su hijo nos ha proporcionado información personal, contácteme para que yo pueda hacer las acciones necesarias.\n" +
                            "\n" +
                            "Cambios a esta política de privacidad\n" +
                            "\n" +
                            "Es posible que actualice nuestra Política de privacidad de vez en cuando. Por lo tanto, se recomienda revisar esta página periódicamente para cualquier cambio. Le notificaré cualquier cambio mediante la publicación de la nueva Política de Privacidad en esta página. Estos cambios entran en vigencia inmediatamente después de que se publiquen en esta página.\n" +
                            "\n" +
                            "Contáctenos\n" +
                            "\n" +
                            "Si tiene alguna pregunta o sugerencia sobre mi Política de privacidad, no dude en ponerse en contacto conmigo.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void _loadFrenchValues() {
        try {
            frenchObject = new JSONObject();
            frenchObject.put("QUOTE_FRAGMENT_TITLE", "Citation du jour");
            frenchObject.put("SHARE_FRAGMENT_TITLE", "Partager la citation du jour");
            frenchObject.put("SETTINGS_FRAGMENT_TITLE", "Paramètres");
            frenchObject.put("SHARE_FRAGMENT_GROUPS_SETTING_TITLE", "Mes Groupes");
            frenchObject.put("SHARE_FRAGMENT_SHARE_WITH_GROUPS_BUTTON", "PARTAGER AVEC DES GROUPES");
            frenchObject.put("SHARE_FRAGMENT_SHARE_THROUGH_ANOTHER_APP_BUTTON", "PARTAGER POUR UNE AUTRE APP");
            frenchObject.put("NAVIGATION_QUOTE_TITLE", "Citations");
            frenchObject.put("NAVIGATION_SHARE_TITLE", "Partager");
            frenchObject.put("NAVIGATION_SETTINGS_TITLE", "Paramètres");
            frenchObject.put("SETTINGS_FRAGMENT_LANGUAGE_TITLE", "La Langue");
            frenchObject.put("SETTINGS_FRAGMENT_NOTIFICATION_TITLE", "Notification");
            frenchObject.put("SETTINGS_FRAGMENT_ALERT_TITLE", "Alerte");
            frenchObject.put("SETTINGS_FRAGMENT_FREQUENCY_TITLE", "La Fréquence");
            frenchObject.put("SETTINGS_FRAGMENT_TIME_TITLE", "Temps");
            frenchObject.put("ALERT_GROUPS_MANAGEMENT_MY_GROUPS_TITLE", "Mes Groupes");
            frenchObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_A_GROUP_BUTTON", "CRÉER UN GROUPE");
            frenchObject.put("ALERT_GROUPS_MANAGEMENT_DELETE_CONFIRMATION_TITLE", "Confirmer la suppression");
            frenchObject.put("ALERT_GROUPS_MANAGEMENT_DELETE_CONFIRMATION_MESSAGE", "Supprimer PLACEHOLDER?");
            frenchObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_MAIN_TITLE", "Créer un Groupe");
            frenchObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_FIELD_TITLE", "Nom de Groupe");
            frenchObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_DONE_BUTTON", "CRÉER");
            frenchObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_BLANK_MESSAGE", "Groupe ne peut pas être vide!");
            frenchObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_ALREADY_EXISTS_MESSAGE", "Le Groupe existe déjà!");
            frenchObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_SUCCESSFUL_CREATION_MESSAGE", "Groupe créé avec succès!");
            frenchObject.put("GROUP_MEMBER_COUNT_MESSAGE_HAS_NO_MEMBERS", "n'a aucun membre");
            frenchObject.put("GROUP_MEMBER_COUNT_MESSAGE_HAS_1_MEMBER", "a 1 membre");
            frenchObject.put("GROUP_MEMBER_COUNT_MESSAGE_HAS_MULTIPLE_MEMBERS", "a PLACEHOLDER membres");
            frenchObject.put("YES", "OUI");
            frenchObject.put("NO", "NON");
            frenchObject.put("DONE", "TERMINÉ");
            frenchObject.put("GROUP", "Groupe");
            frenchObject.put("CONTACTS", "Contacts");
            frenchObject.put("LOADING", "Chargement ...");
            frenchObject.put("SHARE_MESSAGE_PART_AUTHOR", "La citation est de l'auteur");
            frenchObject.put("SHARE_MESSAGE_PART_MESSAGE", "Message de l'application Android 'Papi Quotes'!");
            frenchObject.put("SHARE_MESSAGE_PART_VIA", "Partager le message via");
            frenchObject.put("SHARE_MESSAGE_PART_RECEIVING", "Vous recevez un message de l'application Android 'Papi Quotes'!");
            frenchObject.put("ALERT_GROUPS_MANAGEMENT_ADD_MEMBERS_BUTTON", "AJOUTER DES MEMBRESS");
            frenchObject.put("ABOUT", "Sur");
            frenchObject.put("PORTFOLIO_FILENAME", "portfolio_french.html");
            frenchObject.put("VIEW", "VUE");
            frenchObject.put("VIBRATE", "Vibrer");
            frenchObject.put("SOUND", "Du Son");
            frenchObject.put("DAILY", "Du Quotidien");
            frenchObject.put("WEEKLY", "Hebdomadaire");
            frenchObject.put("PRIVACY_POLICY", "Politique de Confidentialité");
            frenchObject.put("PRIVACY_POLICY_CONTENTS",
                    "Haroldo Paulino a créé l'application Papi Quotes en tant qu'application gratuite. Ce SERVICE est fourni par Haroldo Paulino sans frais et est destiné à être utilisé tel quel.\n" +
                            "\n" +
                            "Cette politique est utilisée pour informer les visiteurs de l'application de mes politiques en matière de collecte, d'utilisation et de divulgation des informations personnelles si quelqu'un a décidé d'utiliser mon service.\n" +
                            "\n" +
                            "Si vous choisissez d'utiliser mon service, vous acceptez la collecte et l'utilisation des informations relatives à cette politique. Les informations personnelles que je recueille sont utilisées pour fournir et améliorer le service. Je n'utiliserai ni ne partagerai vos informations avec quiconque, sauf comme décrit dans cette politique de confidentialité.\n" +
                            "\n" +
                            "Les termes utilisés dans cette politique de confidentialité ont les mêmes significations que dans nos conditions générales, qui sont accessibles sur Papi Quotes, sauf indication contraire dans la présente politique de confidentialité.\n" +
                            "\n" +
                            "Collecte d'informations et utilisation\n" +
                            "\n" +
                            "Pour une meilleure expérience, tout en utilisant notre Service, je peux exiger que vous nous fournissiez certaines informations personnellement identifiables, y compris, entre autres, l'accès à l'état du périphérique et aux contacts. Les informations que je demande sont conservées sur votre appareil et ne sont en aucun cas collectées.\n" +
                            "\n" +
                            "L'application utilise des services tiers pouvant collecter des informations permettant de vous identifier.\n" +
                            "\n" +
                            "Lien vers la politique de confidentialité des fournisseurs de services tiers utilisés par l'application\n" +
                            "\n" +
                            "Services Google Play\n" +
                            "Données de journal\n" +
                            "\n" +
                            "Je souhaite vous informer que chaque fois que vous utilisez mon service, en cas d'erreur dans l'application, je collecte des données et des informations (via des produits tiers) sur votre téléphone, appelées données de journal. Ces données de journal peuvent inclure des informations telles que l'adresse IP de votre appareil, le nom de l'appareil, la version du système d'exploitation, la configuration de l'application lors de l'utilisation de mon service, l'heure et la date d'utilisation du service et d'autres statistiques. .\n" +
                            "\n" +
                            "Les fournisseurs de services\n" +
                            "\n" +
                            "Je peux employer des sociétés tierces et des particuliers pour les raisons suivantes:\n" +
                            "\n" +
                            "* Pour faciliter notre service;\n" +
                            "* Fournir le service en notre nom;\n" +
                            "* Effectuer des services liés au service; ou\n" +
                            "* Pour nous aider à analyser l'utilisation de notre service.\n" +
                            "* Je souhaite informer les utilisateurs de ce service que ces tiers ont accès à vos informations personnelles. La raison est d'effectuer les tâches qui leur sont assignées en notre nom. Cependant, ils sont tenus de ne pas divulguer ou utiliser les informations à d'autres fins.\n" +
                            "\n" +
                            "Sécurité\n" +
                            "\n" +
                            "J'apprécie votre confiance à nous fournir vos informations personnelles, nous nous efforçons donc d'utiliser des moyens commercialement acceptables pour le protéger. Mais rappelez-vous qu'aucune méthode de transmission sur Internet ou méthode de stockage électronique n'est sécurisée et fiable à 100%, et je ne peux garantir sa sécurité absolue.\n" +
                            "\n" +
                            "Liens vers d'autres sites\n" +
                            "\n" +
                            "Ce service peut contenir des liens vers d'autres sites. Si vous cliquez sur un lien tiers, vous serez redirigé vers ce site. Notez que ces sites externes ne sont pas exploités par moi. Par conséquent, je vous conseille vivement de consulter la politique de confidentialité de ces sites Web. Je n'ai aucun contrôle et n'assume aucune responsabilité pour le contenu, les politiques de confidentialité ou les pratiques de tout site ou service tiers.\n" +
                            "\n" +
                            "La vie privée des enfants\n" +
                            "\n" +
                            "Ces services ne s'adressent à personne de moins de 13 ans. Je ne recueille pas sciemment des informations personnellement identifiables auprès d'enfants de moins de 13 ans. Si je découvre qu'un enfant de moins de 13 ans m'a fourni des informations personnelles, je les supprime immédiatement de nos serveurs. Si vous êtes un parent ou un tuteur et que vous savez que votre enfant nous a fourni des informations personnelles, contactez-moi pour que je puisse faire les actions nécessaires.\n" +
                            "\n" +
                            "Changements à cette politique de confidentialité\n" +
                            "\n" +
                            "Je peux mettre à jour notre politique de confidentialité de temps à autre. Ainsi, il est conseillé de consulter cette page périodiquement pour toute modification. Je vous informerai de tout changement en publiant la nouvelle politique de confidentialité sur cette page. Ces modifications prennent effet immédiatement après leur publication sur cette page.\n" +
                            "\n" +
                            "Contactez nous\n" +
                            "\n" +
                            "Si vous avez des questions ou des suggestions concernant ma politique de confidentialité, n'hésitez pas à me contacter.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void _loadPortugueseValues() {
        try {
            portuguesObject = new JSONObject();
            portuguesObject.put("QUOTE_FRAGMENT_TITLE", "Citação do Dia");
            portuguesObject.put("SHARE_FRAGMENT_TITLE", "Compartilhar Citação");
            portuguesObject.put("SETTINGS_FRAGMENT_TITLE", "Configuração");
            portuguesObject.put("SHARE_FRAGMENT_GROUPS_SETTING_TITLE", "Meus Grupos");
            portuguesObject.put("SHARE_FRAGMENT_SHARE_WITH_GROUPS_BUTTON", "COMPARTILHAR COM GRUPOS");
            portuguesObject.put("SHARE_FRAGMENT_SHARE_THROUGH_ANOTHER_APP_BUTTON", "COMPARTILHAR POR OUTRA APP");
            portuguesObject.put("NAVIGATION_QUOTE_TITLE", "Citações");
            portuguesObject.put("NAVIGATION_SHARE_TITLE", "Compartilhar");
            portuguesObject.put("NAVIGATION_SETTINGS_TITLE", "Configuração");
            portuguesObject.put("SETTINGS_FRAGMENT_LANGUAGE_TITLE", "Idioma");
            portuguesObject.put("SETTINGS_FRAGMENT_NOTIFICATION_TITLE", "Notificação");
            portuguesObject.put("SETTINGS_FRAGMENT_ALERT_TITLE", "Alerta");
            portuguesObject.put("SETTINGS_FRAGMENT_FREQUENCY_TITLE", "Freqüência");
            portuguesObject.put("SETTINGS_FRAGMENT_TIME_TITLE", "Hora");
            portuguesObject.put("ALERT_GROUPS_MANAGEMENT_MY_GROUPS_TITLE", "Meus Grupos");
            portuguesObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_A_GROUP_BUTTON", "CRIAR GRUPO");
            portuguesObject.put("ALERT_GROUPS_MANAGEMENT_DELETE_CONFIRMATION_TITLE", "Confirme a exclusão");
            portuguesObject.put("ALERT_GROUPS_MANAGEMENT_DELETE_CONFIRMATION_MESSAGE", "Deletar PLACEHOLDER?");
            portuguesObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_MAIN_TITLE", "Criar Grupo");
            portuguesObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_FIELD_TITLE", "Nome do Grupo");
            portuguesObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_DONE_BUTTON", "CRIAR");
            portuguesObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_BLANK_MESSAGE", "O Grupo não pode ficar em branco!");
            portuguesObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_ALREADY_EXISTS_MESSAGE", "O Grupo já existe!");
            portuguesObject.put("ALERT_GROUPS_MANAGEMENT_CREATE_GROUP_SUCCESSFUL_CREATION_MESSAGE", "Grupo criado com sucesso!");
            portuguesObject.put("GROUP_MEMBER_COUNT_MESSAGE_HAS_NO_MEMBERS", "não tem membros");
            portuguesObject.put("GROUP_MEMBER_COUNT_MESSAGE_HAS_1_MEMBER", "tem 1 membro");
            portuguesObject.put("GROUP_MEMBER_COUNT_MESSAGE_HAS_MULTIPLE_MEMBERS", "tem PLACEHOLDER membros");
            portuguesObject.put("YES", "SIM");
            portuguesObject.put("NO", "NÃO");
            portuguesObject.put("DONE", "PRONTO");
            portuguesObject.put("GROUP", "Group");
            portuguesObject.put("CONTACTS", "Contatos");
            portuguesObject.put("LOADING", "Carregando ...");
            portuguesObject.put("SHARE_MESSAGE_PART_AUTHOR", "A citação é do autor");
            portuguesObject.put("SHARE_MESSAGE_PART_MESSAGE", "Mensagem do aplicativo Android 'Papi Quotes'!");
            portuguesObject.put("SHARE_MESSAGE_PART_VIA", "Compartilhe a mensagem via");
            portuguesObject.put("SHARE_MESSAGE_PART_RECEIVING", "Você está recebendo uma mensagem do aplicativo Android 'Papi Quotes'!");
            portuguesObject.put("ALERT_GROUPS_MANAGEMENT_ADD_MEMBERS_BUTTON", "ADICIONAR MEMBROS");
            portuguesObject.put("ABOUT", "Sobre");
            portuguesObject.put("PORTFOLIO_FILENAME", "portfolio_portuguese.html");
            portuguesObject.put("VIEW", "VER");
            portuguesObject.put("VIBRATE", "Vibrar");
            portuguesObject.put("SOUND", "Soar");
            portuguesObject.put("DAILY", "Diariamente");
            portuguesObject.put("WEEKLY", "Semanalmente");
            portuguesObject.put("PRIVACY_POLICY", "Política de Privacidade");
            portuguesObject.put("PRIVACY_POLICY_CONTENTS",
                    "Haroldo Paulino construiu o aplicativo Papi Quotes como um aplicativo gratuito. Este serviço é fornecido por Haroldo Paulino sem nenhum custo e é destinado para uso como é.\n" +
                            "\n" +
                            "Esta política é usada para informar os visitantes do Aplicativo sobre minhas políticas com a coleta, uso e divulgação de Informações Pessoais, caso alguém decida usar meu Serviço.\n" +
                            "\n" +
                            "Se você optar por usar o meu Serviço, você concorda com a coleta e uso de informações em relação a esta política. As Informações Pessoais que eu coleciono são usadas para fornecer e melhorar o Serviço. Não usarei nem compartilharei suas informações com ninguém, exceto conforme descrito nesta Política de Privacidade.\n" +
                            "\n" +
                            "Os termos usados \u200B\u200Bnesta Política de Privacidade têm os mesmos significados que em nossos Termos e Condições, que podem ser acessados \u200B\u200Bem Cotações da Papi, a menos que definido de outra forma nesta Política de Privacidade.\n" +
                            "\n" +
                            "Recolha e Uso de Informação\n" +
                            "\n" +
                            "Para uma melhor experiência, ao usar nosso Serviço, eu posso exigir que você nos forneça algumas informações pessoalmente identificáveis, incluindo, mas não se limitando a, Acesso ao Estado do Dispositivo e Contatos. As informações que eu solicito são retidas no seu dispositivo e não são coletadas por mim de maneira alguma\n" +
                            "\n" +
                            "O aplicativo usa serviços de terceiros que podem coletar informações usadas para identificá-lo.\n" +
                            "\n" +
                            "Link para a política de privacidade de provedores de serviços de terceiros usados \u200B\u200Bpelo aplicativo\n" +
                            "\n" +
                            "Google Play Services\n" +
                            "Dados de Log\n" +
                            "\n" +
                            "Quero informá-lo que sempre que você usar o meu serviço, em caso de erro no aplicativo eu coletar dados e informações (através de produtos de terceiros) em seu telefone chamado Log Data. Esses dados de registro podem incluir informações como o endereço IP do dispositivo, o nome do dispositivo, a versão do sistema operacional, a configuração do aplicativo ao utilizar meu serviço, a hora e a data do seu uso do Serviço e outras estatísticas. .\n" +
                            "\n" +
                            "Provedores de serviço\n" +
                            "\n" +
                            "Eu posso empregar empresas e indivíduos de terceiros devido às seguintes razões:\n" +
                            "\n" +
                            "* Facilitar nosso serviço;\n" +
                            "* Para fornecer o serviço em nosso nome;\n" +
                            "* Para executar serviços relacionados a serviços; ou\n" +
                            "* Para nos ajudar a analisar como nosso Serviço é usado.\n" +
                            "* Quero informar aos usuários deste Serviço que esses terceiros tenham acesso às suas Informações Pessoais. O motivo é executar as tarefas atribuídas a eles em nosso nome. No entanto, eles são obrigados a não divulgar ou usar as informações para qualquer outra finalidade.\n" +
                            "\n" +
                            "Segurança\n" +
                            "\n" +
                            "Eu valorizo \u200B\u200Bsua confiança em nos fornecer suas Informações Pessoais, por isso estamos nos esforçando para usar meios comercialmente aceitáveis \u200B\u200Bde protegê-los. Mas lembre-se de que nenhum método de transmissão pela Internet ou método de armazenamento eletrônico é 100% seguro e confiável, e não posso garantir sua segurança absoluta.\n" +
                            "\n" +
                            "Links para outros sites\n" +
                            "\n" +
                            "Este Serviço pode conter links para outros sites. Se você clicar em um link de terceiros, você será direcionado para esse site. Observe que esses sites externos não são operados por mim. Portanto, aconselho vivamente que você revise a Política de Privacidade desses sites. Não tenho controle e não me responsabilizo pelo conteúdo, políticas de privacidade ou práticas de sites ou serviços de terceiros.\n" +
                            "\n" +
                            "Privacidade infantil\n" +
                            "\n" +
                            "Esses Serviços não abordam ninguém com idade inferior a 13 anos. Não recolho intencionalmente informações de identificação pessoal de crianças com menos de 13 anos. No caso de descobrir que uma criança com menos de 13 anos me forneceu informações pessoais, excluo imediatamente isso de nossos servidores. Se você é pai / mãe ou responsável legal e sabe que seu filho nos forneceu informações pessoais, entre em contato comigo para que eu possa tomar as providências necessárias.\n" +
                            "\n" +
                            "Alterações a esta política de privacidade\n" +
                            "\n" +
                            "Eu posso atualizar nossa Política de Privacidade de tempos em tempos. Assim, aconselhamos que você revise esta página periodicamente para quaisquer alterações. Vou notificá-lo de quaisquer alterações publicando a nova Política de Privacidade nesta página. Estas alterações entram em vigor imediatamente após serem publicadas nesta página.\n" +
                            "\n" +
                            "Contate-Nos\n" +
                            "\n" +
                            "Se você tiver dúvidas ou sugestões sobre minha Política de Privacidade, não hesite em entrar em contato comigo.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
