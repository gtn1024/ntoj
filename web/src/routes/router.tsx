import { createBrowserRouter } from 'react-router-dom'
import { preload } from 'swr'
import type { AxiosError } from 'axios'
import { MainLayout } from '../layouts/MainLayout.tsx'
import { SignInPage } from '../pages/SignInPage.tsx'
import { AdminLayout } from '../layouts/AdminLayout.tsx'
import { useUserStore } from '../stores/useUserStore.tsx'
import { getToken } from '../lib/token.ts'
import { http } from '../lib/Http.tsx'
import { ErrorForbidden, ErrorUnauthorized } from '../errors.ts'
import { ErrorPage } from '../pages/ErrorPage.tsx'
import { AdminHomePage } from '../pages/admin/AdminHomePage.tsx'
import { AdminAnnouncementPage } from '../pages/admin/AdminAnnouncementPage.tsx'
import { AdminAnnouncementEditPage } from '../pages/admin/AdminAnnouncementEditPage.tsx'
import { HomePage } from '../pages/HomePage.tsx'
import { AnnouncementPage } from '../pages/AnnouncementPage.tsx'
import { UserProfilePage } from '../pages/UserProfilePage.tsx'
import { AdminProblemPage } from '../pages/admin/AdminProblemPage.tsx'
import { ProblemListPage } from '../pages/ProblemListPage.tsx'
import { ProblemPage } from '../pages/ProblemPage.tsx'
import { NotFoundPage } from '../pages/404.tsx'

async function rootLoader() {
  const user = useUserStore.getState().user
  if (user.id || !getToken()) {
    return true
  }
  return preload('/auth/current', async (path) => {
    return http.get<CurrentUser>(path)
      .then((res) => {
        useUserStore.setState({
          ...useUserStore.getState(),
          user: res.data.data,
        })
        return true
      })
      .catch((err: AxiosError) => {
        if (err.response?.status === 401)
          throw new ErrorUnauthorized()
        throw err
      })
  })
}

async function adminLoader() {
  return rootLoader()
    .then(() => {
      const user = useUserStore.getState().user
      if (user.role === 'ADMIN' || user.role === 'SUPER_ADMIN') {
        return true
      } else {
        throw new ErrorForbidden()
      }
    })
}

export const router = createBrowserRouter(
  [
    {
      path: '/sign_in',
      element: <SignInPage/>,
    },
    {
      path: '/admin',
      element: <AdminLayout/>,
      errorElement: <ErrorPage/>,
      loader: adminLoader,
      children: [
        { index: true, element: <AdminHomePage/> },
        {
          path: 'announcement',
          children: [
            { index: true, element: <AdminAnnouncementPage/> },
            { path: 'new', element: <AdminAnnouncementEditPage/> },
            { path: ':id/edit', element: <AdminAnnouncementEditPage/> },
          ],
        },
        {
          path: 'problem',
          children: [
            { index: true, element: <AdminProblemPage/> },
          ],
        },
      ],
    },
    {
      path: '/',
      element: <MainLayout/>,
      errorElement: <ErrorPage/>,
      loader: rootLoader,
      children: [
        { index: true, element: <HomePage/> },
        { path: 'about', element: <div>About</div> },
        {
          path: 'problem',
          children: [
            { index: true, element: <ProblemListPage/> },
            { path: ':alias', element: <ProblemPage/> },
          ],
        },
        { path: 'announcement/:id', element: <AnnouncementPage/> },
        { path: 'user/:username', element: <UserProfilePage/> },
      ],
    },
    {
      path: '*',
      element: <NotFoundPage/>,
    },
  ],
)
