<template>
  <footer class="footer">
    <!-- Gradient fon beradigan qism -->
    <div class="footer-gradient">
      <div class="footer-top">
        <!-- Logo va qisqacha matn -->
        <div class="footer-section">
          <img :src="ramzImage" alt="Platform Logo" class="logo" />
          <p class="tagline">Empowering learners with AI-driven education solutions.</p>
        </div>

        <!-- Tezkor havolalar -->
        <div class="footer-section">
          <h3>Quick Links</h3>
          <ul>
            <li><a href="/about">About Us</a></li>
            <li><a href="/courses">Courses</a></li>
            <li><a href="/faq">FAQ</a></li>
            <li><a href="/contact">Contact</a></li>
          </ul>
        </div>

        <!-- Kontakt ma'lumotlari -->
        <div class="footer-section">
          <h3>Contact Us</h3>
          <p>Email: Edux@info.com</p>
          <p>Phone: +123 456 7890</p>
          <p>Address: Tashkent/Uzbekistan</p>
        </div>
      </div>

      <!-- Ijtimoiy tarmoqlar ikonkalari -->
      <!-- <div class="footer-social">
        <a href="#" target="_blank"><i class="fab fa-facebook-f"></i></a>
        <a href="#" target="_blank"><i class="fab fa-twitter"></i></a>
        <a href="#" target="_blank"><i class="fab fa-instagram"></i></a>
        <a href="#" target="_blank"><i class="fab fa-linkedin-in"></i></a>
      </div> -->
    </div>

    <!-- Quyidagi satr butun kenglikda, matn markazda -->
    <div class="footer-bottom">&copy; {{ new Date().getFullYear() }} OnlineCoursePlatform. All rights reserved.</div>
  </footer>
</template>

<script lang="ts">
import ramzLogo from '@/assets/images/image1.png'; // Logo rasmi

export default {
  name: 'JhiFooter',
  data() {
    return {
      ramzImage: ramzLogo,
    };
  },
};
</script>

<style scoped>
/* Footer butun ekranni egallashi uchun */
.footer {
  width: 100%;
  margin: 0;
  padding: 0;
  color: #fff;
  font-family: sans-serif;
}

/* Gradient fon */
.footer-gradient {
  background: linear-gradient(90deg, #2c3e50, #34495e);
  width: 100%;
  padding: 20px 0; /* faqat vertikal bo'sh joy */
  margin: 0;
}

/* Ustki qism: logo, quick links, contact */
.footer-top {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  /* Gorizontal tomonda bo'sh joy yo'q,
     faqat minimal vertikal uchun padding-top/bottom berish mumkin */
  padding: 0;
  margin: 0;
  width: 100%;
}

.footer-section {
  flex: 1 1 200px; /* responsiv bo'lishi uchun */
  min-width: 200px;
  padding: 10px; /* Ichkaridan biroz joy beramiz */
  box-sizing: border-box;
}

/* Logo */
.logo {
  width: 120px;
  margin-bottom: 10px;
}

/* Qisqacha matn */
.tagline {
  margin: 0;
  font-size: 0.9rem;
  line-height: 1.4;
}

/* Quick Links */
.footer-section ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

.footer-section li {
  margin: 5px 0;
}

.footer-section a {
  color: #f1c40f;
  text-decoration: none;
  transition: color 0.3s ease;
}

.footer-section a:hover {
  color: #f39c12;
}

/* Ijtimoiy tarmoqlar ikonkalari */
.footer-social {
  width: 100%;
  text-align: center;
  margin-top: 20px;
}

.footer-social a {
  display: inline-block;
  color: #fff;
  font-size: 1.2rem;
  margin: 0 10px;
  transition: color 0.3s ease;
}

.footer-social a:hover {
  color: #f1c40f;
}

/* Pastki qism (copyright) */
.footer-bottom {
  background: #222;
  width: 100%;
  text-align: center;
  padding: 15px 0;
  font-size: 0.85rem;
  margin: 0;
}
</style>
