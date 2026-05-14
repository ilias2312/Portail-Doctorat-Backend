package com.emsi.doctorat_portal.enums;

public enum StatutDossier {
    EN_ATTENTE_DIRECTEUR,
    EN_ATTENTE_ADMIN,
    VALIDE_ADMIN,      // AJOUTER CETTE LIGNE (C'est elle qui cause l'erreur 500)
    VALIDE,
    REJETE,
    REJETE_DIRECTEUR
}