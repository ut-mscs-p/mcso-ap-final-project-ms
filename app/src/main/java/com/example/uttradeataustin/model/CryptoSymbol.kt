package com.example.uttradeataustin.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class CryptoSymbol(
    var symbol: String = "",
    // Auth information
    var ownerName: String = "",
    var ownerUid: String = "",
    @ServerTimestamp val timeStamp: Timestamp? = null,
    // firestoreID is generated by firestore, used as primary key
    @DocumentId
    var firestoreID: String = ""
)