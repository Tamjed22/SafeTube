# Safe Tube — Android starter project

এটি Safe Tube-এর প্রথম Android Studio প্রজেক্ট (Kotlin + Jetpack Compose)।

## এখন কী কাজ করছে
- Light theme UI
- Home, Channels, Add Channels, Library, Settings
- Demo channel add/remove
- Home feed-এ subscribed channel ID allow-list দিয়ে filtering
- Demo video card tap করলে placeholder dialog

## এখনো কী যুক্ত হয়নি
- YouTube Data API থেকে live channel search/video feed
- প্রকৃত YouTube player integration
- Firebase login/database/notifications
- Production privacy policy, consent flow, API quota handling
- Google Play release signing এবং policy review

বর্তমান চ্যানেল ও ভিডিওগুলো কেবল UI পরীক্ষা করার নমুনা। এগুলো লাইভ YouTube ডেটা নয়।

## Android Studio-তে চালানোর নিয়ম
1. ZIP ফাইল Extract করুন।
2. Android Studio খুলে **Open** নির্বাচন করুন এবং `SafeTube-Android` ফোল্ডারটি খুলুন।
3. Gradle sync শেষ হতে দিন।
4. Android ফোনে USB debugging চালু করে Run চাপুন, অথবা Emulator নির্বাচন করুন।
5. APK তৈরি করতে: **Build > Build Bundle(s) / APK(s) > Build APK(s)**।

## লাইভ YouTube সংযোগের পরবর্তী কাজ
- Google Cloud Console-এ project তৈরি করে YouTube Data API v3 enable করতে হবে।
- API key সরাসরি APK-তে রাখা নিরাপদ নয়; production-এ backend proxy ও quota/rate limiting ব্যবহার করুন।
- YouTube API Services Terms, Developer Policies এবং branding/player requirements মেনে channel search, channel uploads playlist ও video metadata আনতে হবে।
- প্রতিটি ব্যবহারকারীর নিজের Safe Tube channel list database-এ সংরক্ষণ করতে হবে।
- Home feed render করার আগে প্রতিটি ভিডিওর `channelId` ওই ব্যবহারকারীর allow-list-এ আছে কি না যাচাই করতে হবে।
- YouTube-এর বাধ্যতামূলক player UI/ads/related features পরিবর্তন বা সরিয়ে ফেলা যাবে না। Play Store প্রকাশের আগে নীতিমালা পর্যালোচনা প্রয়োজন।

## Package
`com.safetube.app`

## Build environment
JDK 17 এবং Android SDK Platform 35 সুপারিশ করা হচ্ছে।
