# Banking System ERD

## Overview
This ERD is designed based on the requirements for Account, Balance/Transaction, Limit Management, and Transfer features.

### Key Features
- **Account**: Supports soft delete (`status`).
- **Daily Limit**: Tracks daily withdrawal and transfer amounts separately using `DAILY_LIMIT_USAGE`.
- **Transaction**: Records all flow (Deposit, Withdraw, Transfer) with support for `OUT`/`IN` direction for transfers.

```mermaid
erDiagram
    ACCOUNT {
        bigint id PK
        varchar account_number UK "Unique Account Number"

        decimal balance "Current Balance"
        varchar status "ACTIVE, DELETED"
        datetime created_at
        datetime updated_at
    }

    DAILY_LIMIT_USAGE {
        bigint id PK
        bigint account_id FK
        date date "YYYY-MM-DD (Asia/Seoul)"
        decimal total_withdraw_amount "Daily accumulated withdraw"
        decimal total_transfer_amount "Daily accumulated transfer"
        datetime updated_at
        bigint version "Optimistic Lock"
    }

    TRANSACTION {
        bigint id PK
        bigint account_id FK "Owner of this log"
        varchar type "DEPOSIT, WITHDRAW, TRANSFER"
        varchar direction "IN, OUT"
        decimal amount "Transaction Amount"
        decimal fee "Fee (1% for Transfer)"
        decimal balance_snapshot "Balance after transaction"
        bigint counterparty_account_id "Sender/Receiver Account ID"
        string description "Transaction Note"
        datetime created_at
    }

    ACCOUNT ||--o{ DAILY_LIMIT_USAGE : "tracks daily usage"
    ACCOUNT ||--o{ TRANSACTION : "logs history"
```

## Table Details

### 1. ACCOUNT
- Stores account information.
- `account_number`: Must be unique.
- `status`: Used for soft delete (F-1.2).
- `balance`: Current balance.

### 2. DAILY_LIMIT_USAGE
- Tracks daily usage for withdrawal and transfer limits (F-3.1, F-3.2).
- `date`: Based on Asia/Seoul 00:00~23:59.
- `version`: For optimistic locking to handle concurrency.

### 3. TRANSACTION
- Logs all financial activities (F-5.1).
- `direction`: `IN` (Deposit, Transfer Receive), `OUT` (Withdraw, Transfer Send).
- `fee`: Recorded separately (e.g., for Transfer OUT).
- `counterparty_account_id`: Stores the other party's account ID for transfers.
