# Rustic Orchard - Local Farm-to-Table Marketplace

A high-performance, native Android farm-to-table marketplace built with **Kotlin** and **Jetpack Compose**. It provides direct commerce between local growers and consumers, featuring declarative UI state management, real-time inventory coordination, precision packaging and weight calculators, invoice generation with system sharing, split-screen farm map discovery, live delivery tracking, direct buyer-vendor messaging, and Gemini AI product recommendations.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following technical and product decisions are confirmed for this build:

- **Technology Stack**: **Native Android with Kotlin & Jetpack Compose (Material 3)**. Provides 100% native performance, fluid 60fps animations, seamless Android System Share Sheet integration for invoices, and reactive declarative state architecture.
- **Confirmed Role Experience**: **Dual-mode toggle** allowing instant switching between **Buyer Mode** (shopping, split map discovery, orders, chat, reviews) and **Farmer / Admin Mode** (inventory control, packaging calculator, admin analytics, order fulfillment, tax compliance).
- **Confirmed Discovery UX**: **Split View** combining an interactive visual map with farm pins and dynamic nearby farm cards for seamless browsing and pickup coordination.
- **Confirmed Visual Identity**: **Rustic Orchard** aesthetic featuring deep forest green primary surfaces (`#1B4332`), golden honey accents (`#E9C46A`), warm parchment backgrounds (`#FAF8F5`), and organic harvest badge styling.
- **Intelligence & Persistence**: Local reactive repository + Room database architecture for instant cross-role updates, and direct REST integration with `gemini-3.5-flash` for personalized seasonal produce recommendations.

---

## 1. Overview & Core Concept

### What It Does
**Rustic Orchard** brings local agriculture directly to consumers with a modern, transparent mobile marketplace delivering all 13 core requirements:
1. **Produce Configuration & Invoicing**: Custom weight entry (lbs/kg), price calculation, package weights, box counts, dynamic cart management, and one-tap formatted invoice generation with Android system share sheet support.
2. **Real-Time Inventory Updates**: Live stock tracking with instant updates when orders are placed or restocked by farmers.
3. **Secure Checkout & Multi-Gateway Payments**: Card processing, Mobile Wallets (Google Pay style), and FarmPay Direct with masked encryption badges and order validation.
4. **Split Map Farm Discovery**: Interactive Canvas-based farm locator with radius filtering, farm badges, and pickup slot selectors.
5. **Push & Restock Alerts**: In-app notifications center with restock announcements and order status milestone alerts.
6. **Transparent Review System**: Star ratings, verified buyer tags, and structured community feedback.
7. **Automated Tax & Regional Compliance**: Automatic regional agricultural sales tax calculations (7.5%) and exportable compliance summaries.
8. **Farmer & Admin Analytics Dashboard**: Inventory turnover, revenue metrics, package sales volume, and customer activity charts.
9. **Direct Vendor Chat**: Real-time messaging thread between buyer and farm vendor per order or general inquiries.
10. **Delivery & Pickup Tracking**: Live visual stepper with GPS-like courier/farm truck route simulation, ETA countdown, and driver contact.
11. **AI-Driven Product Recommendations**: Gemini-powered seasonal produce assistant recommending items based on dietary preferences, seasonal harvest, and purchase history.

### Target Audience & Persona
- **Consumers & CSA Members**: Seeking pesticide-free, organic, pasture-raised local produce, milk, eggs, honey, and artisan orchard goods.
- **Local Farmers & Orchardists**: Small-to-medium family farms needing simple mobile tools to price harvests, manage package weights, produce formal invoices, and communicate with buyers without middleman fees.

---

## 2. User Experience & Visual Design

### Key User Flows

```
┌────────────────────────────────────────────────────────────────────────┐
│                          Role Toggle Top Bar                           │
│              [ Buyer Mode 🍏 ]  ⇄  [ Farmer / Admin Mode 🚜 ]           │
└────────────────────────────────────────────────────────────────────────┘
                                    │
         ┌──────────────────────────┴──────────────────────────┐
         ▼                                                     ▼
┌─────────────────────────────────┐   ┌─────────────────────────────────┐
│           BUYER FLOWS           │   │          FARMER FLOWS           │
│ 1. Split Map & Farm Discovery   │   │ 1. Add / Edit Harvest Produce   │
│ 2. Product Detail & Weight Calc │   │ 2. Package Weight & Stock Setup │
│ 3. Cart & Invoice Generator     │   │ 3. Admin Sales & Tax Compliance │
│ 4. Multi-Gateway Payment Checkout│  │ 4. Order Fulfillment & Dispatch │
│ 5. Real-Time Tracking & Chat    │   │ 5. Live Chat With Customers     │
│ 6. AI "Seasonal Pantry" Recs    │   │ 6. Inventory Restock Alerts     │
└─────────────────────────────────┘   └─────────────────────────────────┘
```

1. **Buyer Discovery (Split View)**:
   - Upper screen: Interactive vector-drawn farm map displaying nearby farm stalls, radius circles, and active pins.
   - Lower screen: Horizontally scrollable and vertically expandable list of nearby farms with distance, rating, and available inventory count.
2. **Product Configuration & Invoicing**:
   - Buyers pick items (e.g., Honeycrisp Apples, Raw Wildflower Honey, Heirloom Tomatoes).
   - Enter desired weight (lbs/kg), select package size, specify number of packages, and inspect automated subtotal calculations.
   - One-tap **"Generate Invoice"** modal displays a formatted bill with invoice number, date, item breakdown, tax, and share button (via `Intent.ACTION_SEND`).
3. **Checkout & Multi-Gateway Payment**:
   - Select Delivery vs. Farm Pickup.
   - Payment options: Credit/Debit Card, Mobile Wallet (Google Pay), or Local FarmPay.
   - Automated regional tax calculation applied with itemized summary.
4. **Order Tracking & Chat**:
   - Live delivery progress indicator (Order Confirmed → Packed at Farm → Out for Delivery → Arrived).
   - Dedicated "Message Farmer" button opening an integrated chat thread with preset quick replies and custom messaging.
5. **Farmer & Admin Management**:
   - Quick addition of new harvests: title, category, stock weight, price per lb, default package weight, farm name, and location.
   - Analytics overview: revenue, active orders, top-selling produce, and regional compliance tax logs.

### Visual Identity & Theme ("Rustic Orchard")
- **Primary Color**: Deep Forest Green (`#1B4332` / `#2D6A4F`) – conveying growth, nature, and agriculture.
- **Secondary Accent**: Golden Honey (`#D4A373` / `#E9C46A`) – warm, artisanal, sun-drenched harvest tone.
- **Tertiary Accent**: Terracotta Rust (`#BC6C25`) – earthy and rustic.
- **Surfaces & Background**:
  - Light: Warm Organic Parchment (`#FAF8F5`), Cream card surfaces (`#FFFFFF`).
  - Dark: Deep Pine Night (`#121915`), Dark spruce container (`#1E2822`).
- **Typography**: Clean, robust hierarchy with legible numbers for weights, package counts, and currency.
- **Touch Targets & Elevation**: Minimum 48dp touch targets, smooth ripples, and rounded container surfaces.

---

## 3. Key Product Decisions & Trade-Offs

| Decision | Chosen Approach | Rationale | Alternatives Considered |
|---|---|---|---|
| **Language & UI Framework** | Kotlin + Jetpack Compose | Native execution, official build-chain support in AI Studio, declarative reactive state similar to Flutter, zero overhead. | Flutter/Dart (not supported in current cloud container toolchain). |
| **Role Architecture** | Dynamic Dual-Role Switch in App Bar | Allows instant switching between Buyer and Farmer flows for comprehensive demonstration and testing. | Hardcoded single role or separate APKs. |
| **Map Visualization** | High-performance Compose Canvas Interactive Map with pin markers & radius slider | Instant rendering, offline support, touch-responsive panning, zero API key friction. | Google Maps SDK requiring external billing and restricted container API keys. |
| **AI Recommendations** | Gemini 3.5 Flash direct REST integration with intelligent fallback heuristics | Delivers fast contextual recommendations based on shopping cart ingredients and season. | Client-only mock. |
| **Persistence & State** | In-Memory Reactive State Repository with Room Database architecture | Guarantees instant synchronization between buyer orders and farmer inventory updates across screen changes. | Isolated component states (would lose data when switching roles). |
| **Invoice Output** | Rich Material Invoice Dialog with Android System Share Intent | Formats items, weights, batch codes, and tax into clean shareable text/receipt document. | Heavy PDF generation library. |

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                           UI Layer (Jetpack Compose)                   │
│  ┌─────────────────────────┐  ┌──────────────────────────────────────┐ │
│  │       Buyer Views       │  │             Farmer Views             │ │
│  │ • Split Map & Catalog   │  │ • Inventory Management & Restock     │ │
│  │ • Produce Weight Config │  │ • Admin Dashboard & Analytics        │ │
│  │ • Invoice & Cart Dialog │  │ • Regional Tax Compliance Logs       │ │
│  │ • Order Tracker & Chat  │  │ • Direct Order Fulfillment & Chat    │ │
│  └───────────┬─────────────┘  └──────────────────┬───────────────────┘ │
└──────────────┼───────────────────────────────────┼─────────────────────┘
               │                                   │
               ▼                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                     ViewModel & State Coordinators                     │
│  • MarketplaceViewModel: Exposes products, cart, active role, filters  │
│  • OrderTrackingViewModel: Manages live status milestones & courier    │
│  • ChatViewModel: Manages buyer-farmer message threads                 │
│  • AiRecommendationViewModel: Interfaces with Gemini 3.5 Flash API     │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        Data & Repository Layer                         │
│  ┌─────────────────────────┐  ┌──────────────────────────────────────┐ │
│  │ FarmRepository (State)  │  │ GeminiApiService (Retrofit REST)     │ │
│  │ • Products & Inventory  │  │ • Model: gemini-3.5-flash            │ │
│  │ • Orders & Invoices     │  │ • Generates pairings & recipe tips   │ │
│  │ • Ratings & Reviews     │  └──────────────────────────────────────┘ │
│  │ • Chats & Notifications │                                           │
│  └─────────────────────────┘                                           │
└────────────────────────────────────────────────────────────────────────┘
```

### Core Entities & Models
- `FarmProduct`: ID, name, farmName, farmLocation, category, pricePerLb, currentStockWeight, packageWeight, unit, rating, organicCertified, description.
- `CartItem`: Product reference, userSelectedWeight, packageWeight, packageCount, calculatedPrice.
- `Invoice`: InvoiceNumber, date, buyerName, farmName, items, subtotal, taxAmount, deliveryFee, totalAmount, paymentStatus, paymentMethod.
- `Order`: ID, items, total, status (`PLACED`, `PACKED`, `IN_TRANSIT`, `DELIVERED`), trackingCoordinates, etaMinutes, farmId, buyerId.
- `ChatMessage`: ID, senderName, isFarmer, text, timestamp, orderReferenceId.
- `FarmReview`: ID, farmId, authorName, rating, date, reviewText, verifiedPurchase.
- `FarmMarker`: ID, farmName, latitude/longitude offsets, specialty, rating, distanceMiles.

---

## 5. Implementation Roadmap

1. **Phase 1: Dependencies, Theme & Base Tokens**
   - Configure Gradle dependencies (serialization, Retrofit, icons-extended).
   - Set up `Theme.kt` with Rustic Orchard color scheme, shapes, and typography.
   - Update `metadata.json` and `strings.xml` with app branding: **Rustic Orchard - Farm Marketplace**.
2. **Phase 2: Data Models & Mock-Integrated Reactive Repository**
   - Implement `FarmProduct`, `CartItem`, `Invoice`, `Order`, `ChatMessage`, and `FarmReview`.
   - Build `FarmRepository` with pre-seeded local farms, fresh seasonal produce, inventory update listeners, and order dispatch channels.
3. **Phase 3: Core Buyer Experience**
   - **Split View Screen**: Custom canvas interactive map with touchable farm markers, distance filter, and synchronized bottom produce cards.
   - **Product Details & Package Weight Configurator**: Dynamic weight, package count, and price calculation.
   - **Invoice Generator**: Comprehensive modal displaying formatted bill with print/share action.
4. **Phase 4: Checkout, Multi-Gateway Payment & Tax Engine**
   - Payment sheet with credit cards, mobile wallet, and FarmPay direct.
   - Automated 7.5% regional agricultural tax calculation with tax compliance report generation.
5. **Phase 5: Real-Time Order Tracking & Direct Chat**
   - Visual step tracker with simulated courier movement on map and estimated time of arrival.
   - Bidirectional messaging thread with quick farmer response prompts.
6. **Phase 6: Farmer & Admin Dashboard**
   - Inventory editor: adjust stock levels, restock notifications trigger, update pricing/package weight.
   - Admin analytics cards: revenue, top-selling harvests, packaging metrics, tax liability log.
7. **Phase 7: AI Fresh Picks & Review System**
   - Gemini 3.5 Flash integration for contextual seasonal recommendations based on cart contents.
   - Interactive star rating and review creation dialog with verified buyer badge.
8. **Phase 8: Compilation, Verification & Polish**
   - Verify zero build warnings or compilation errors with `compile_applet`.
   - Test mode transitions, screen rotation adaptability, and touch targets.
