import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import 'leaflet/dist/leaflet.css'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

// Iconos MDI
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import '@mdi/font/css/materialdesignicons.css'

const vuetify = createVuetify({
	components,
	directives,
	icons: {
		defaultSet: 'mdi',
		aliases,
		sets: {
			mdi,
		},
	},
	theme: {
		defaultTheme: 'dark',
		themes: {
			dark: {
				colors: {
					primary: '#1976D2',
					secondary: '#424242',
					success: '#2E7D32',
					info: '#0288D1',
					warning: '#F9A825',
					error: '#D32F2F',
				},
			},
			light: {
				colors: {
					primary: '#1976D2',
				},
			},
		},
	},
})

const app = createApp(App)
app.use(router)
app.use(vuetify)
app.mount('#app')
