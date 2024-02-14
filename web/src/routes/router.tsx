import { createBrowserRouter } from 'react-router-dom'
import { preload } from 'swr'
import type { AxiosError } from 'axios'
import { lazy } from 'react'
import { MainLayout } from '../layouts/MainLayout.tsx'
import { SignInPage } from '../pages/SignInPage.tsx'
import { AdminLayout } from '../layouts/AdminLayout.tsx'
import { useUserStore } from '../stores/useUserStore.tsx'
import { getToken } from '../lib/token.ts'
import { http } from '../lib/Http.tsx'
import { ErrorForbidden, ErrorUnauthorized } from '../errors.ts'
import { ErrorPage } from '../pages/ErrorPage.tsx'
import { ContestLayout } from '../layouts/ContestLayout.tsx'

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

const AdminHomePage = lazy(() => import('../pages/admin/AdminHomePage'))
const AdminAnnouncementPage = lazy(() => import('../pages/admin/AdminAnnouncementPage'))
const AdminUserPage = lazy(() => import('../pages/admin/AdminUserPage'))
const AdminUserEditPage = lazy(() => import('../pages/admin/AdminUserEditPage'))
const RecordPage = lazy(() => import('../pages/RecordPage'))
const ProblemPage = lazy(() => import('../pages/ProblemPage'))
const RecordListPage = lazy(() => import('../pages/RecordListPage'))
const AdminAnnouncementEditPage = lazy(() => import('../pages/admin/AdminAnnouncementEditPage'))
const HomePage = lazy(() => import('../pages/HomePage'))
const AnnouncementPage = lazy(() => import('../pages/AnnouncementPage'))
const UserProfilePage = lazy(() => import('../pages/UserProfilePage'))
const AdminProblemPage = lazy(() => import('../pages/admin/AdminProblemPage'))
const ProblemListPage = lazy(() => import('../pages/ProblemListPage'))
const NotFoundPage = lazy(() => import('../pages/404'))
const AdminProblemEditPage = lazy(() => import('../pages/admin/AdminProblemEditPage'))
const AdminLanguagePage = lazy(() => import('../pages/admin/AdminLanguagePage'))
const AdminLanguageEditPage = lazy(() => import('../pages/admin/AdminLanguageEditPage'))
const AdminJudgeClientTokenPage = lazy(() => import('../pages/admin/AdminJudgeClientTokenPage'))
const AdminJudgeClientTokenEditPage = lazy(() => import('../pages/admin/AdminJudgeClientTokenEditPage'))
const AdminContestPage = lazy(() => import('../pages/admin/AdminContestPage'))
const AdminContestEditPage = lazy(() => import('../pages/admin/AdminContestEditPage'))
const ContestListPage = lazy(() => import('../pages/ContestListPage'))
const ContestHome = lazy(() => import('../pages/contest/ContestHome'))
const ContestProblemList = lazy(() => import('../pages/contest/ContestProblemList'))
const ContestProblem = lazy(() => import('../pages/contest/ContestProblem'))
const ContestClarificationListPage = lazy(() => import('../pages/contest/ContestClarificationListPage'))
const ContestNewClarificationPage = lazy(() => import('../pages/contest/ContestNewClarificationPage'))
const ContestClarificationDetailPage = lazy(() => import('../pages/contest/ContestClarificationDetailPage'))
const ContestSubmissionListPage = lazy(() => import('../pages/contest/ContestSubmissionListPage'))
const ContestStandingPage = lazy(() => import('../pages/contest/ContestStandingPage'))

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
          path: 'user',
          children: [
            { index: true, element: <AdminUserPage/> },
            { path: 'new', element: <AdminUserEditPage/> },
            { path: ':id/edit', element: <AdminUserEditPage/> },
          ],
        },
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
            { path: 'new', element: <AdminProblemEditPage/> },
            { path: ':id/edit', element: <AdminProblemEditPage/> },
          ],
        },
        {
          path: 'contest',
          children: [
            { index: true, element: <AdminContestPage/> },
            { path: 'new', element: <AdminContestEditPage/> },
            { path: ':id/edit', element: <AdminContestEditPage/> },
          ],
        },
        {
          path: 'language',
          loader: async () => {
            await adminLoader()
            const user = useUserStore.getState().user
            if (user.role === 'SUPER_ADMIN') {
              return true
            } else {
              throw new ErrorForbidden()
            }
          },
          children: [
            { index: true, element: <AdminLanguagePage/> },
            { path: 'new', element: <AdminLanguageEditPage/> },
            { path: ':id/edit', element: <AdminLanguageEditPage/> },
          ],
        },
        {
          path: 'judge_client_token',
          loader: async () => {
            await adminLoader()
            const user = useUserStore.getState().user
            if (user.role === 'SUPER_ADMIN') {
              return true
            } else {
              throw new ErrorForbidden()
            }
          },
          children: [
            { index: true, element: <AdminJudgeClientTokenPage/> },
            { path: 'new', element: <AdminJudgeClientTokenEditPage/> },
            { path: ':id/edit', element: <AdminJudgeClientTokenEditPage/> },
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
          path: 'p',
          children: [
            { index: true, element: <ProblemListPage/> },
            { path: ':alias', element: <ProblemPage/> },
          ],
        },
        { path: 'a/:id', element: <AnnouncementPage/> },
        { path: 'u/:username', element: <UserProfilePage/> },
        {
          path: 'r',
          children: [
            { index: true, element: <RecordListPage/> },
            { path: ':id', element: <RecordPage/> },
          ],
        },
        {
          path: 'c',
          children: [
            { index: true, element: <ContestListPage/> },
          ],
        },
      ],
    },
    {
      path: '/c/:id',
      element: <ContestLayout/>,
      errorElement: <ErrorPage/>,
      loader: rootLoader,
      children: [
        { index: true, element: <ContestHome/> },
        {
          path: 'p',
          children: [
            { index: true, element: <ContestProblemList/> },
            { path: ':alias', element: <ContestProblem/> },
          ],
        },
        {
          path: 'clarification',
          children: [
            { index: true, element: <ContestClarificationListPage/> },
            { path: 'new', element: <ContestNewClarificationPage/> },
            { path: ':clarificationId', element: <ContestClarificationDetailPage/> },
          ],
        },
        {
          path: 'submission',
          children: [
            { index: true, element: <ContestSubmissionListPage/> },
          ],
        },
        {
          path: 'standing',
          element: <ContestStandingPage/>,
        },
      ],
    },
    {
      path: '*',
      element: <NotFoundPage/>,
    },
  ],
)
