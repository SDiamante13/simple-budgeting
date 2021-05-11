db.createUser(
    {
        user : "steven",
        pwd : "pass",
        roles : [
            {
                role : "readWrite",
                db : "test"
            }
        ]
    }
)