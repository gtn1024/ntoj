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
import { ContestLayout } from '../layouts/ContestLayout.tsx'
import NotFoundPage from '../pages/404.tsx'
import AnnouncementPage from '../pages/AnnouncementPage.tsx'
import ContestListPage from '../pages/ContestListPage.tsx'
import HomePage from '../pages/HomePage.tsx'
import ProblemListPage from '../pages/ProblemListPage.tsx'
import ProblemPage from '../pages/ProblemPage.tsx'
import RecordListPage from '../pages/RecordListPage.tsx'
import RecordPage from '../pages/RecordPage.tsx'
import UserProfilePage from '../pages/UserProfilePage.tsx'
import AdminAnnouncementEditPage from '../pages/admin/AdminAnnouncementEditPage.tsx'
import AdminAnnouncementPage from '../pages/admin/AdminAnnouncementPage.tsx'
import AdminContestEditPage from '../pages/admin/AdminContestEditPage.tsx'
import AdminContestPage from '../pages/admin/AdminContestPage.tsx'
import AdminHomePage from '../pages/admin/AdminHomePage.tsx'
import AdminJudgeClientTokenEditPage from '../pages/admin/AdminJudgeClientTokenEditPage.tsx'
import AdminJudgeClientTokenPage from '../pages/admin/AdminJudgeClientTokenPage.tsx'
import AdminLanguageEditPage from '../pages/admin/AdminLanguageEditPage.tsx'
import AdminLanguagePage from '../pages/admin/AdminLanguagePage.tsx'
import AdminProblemEditPage from '../pages/admin/AdminProblemEditPage.tsx'
import AdminProblemPage from '../pages/admin/AdminProblemPage.tsx'
import AdminUserEditPage from '../pages/admin/AdminUserEditPage.tsx'
import AdminUserImportPage from '../pages/admin/AdminUserImportPage.tsx'
import AdminUserPage from '../pages/admin/AdminUserPage.tsx'
import ContestClarificationDetailPage from '../pages/contest/ContestClarificationDetailPage.tsx'
import ContestClarificationListPage from '../pages/contest/ContestClarificationListPage.tsx'
import ContestHome from '../pages/contest/ContestHome.tsx'
import ContestNewClarificationPage from '../pages/contest/ContestNewClarificationPage.tsx'
import ContestProblem from '../pages/contest/ContestProblem.tsx'
import ContestProblemList from '../pages/contest/ContestProblemList.tsx'
import ContestStandingPage from '../pages/contest/ContestStandingPage.tsx'
import ContestSubmissionListPage from '../pages/contest/ContestSubmissionListPage.tsx'
import { ArticleListPage } from '../pages/ArticleListPage.tsx'
import { ArticleViewPage } from '../pages/ArticleViewPage.tsx'
import { ArticleEditPage } from '../pages/ArticleEditPage.tsx'

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
      element: <SignInPage />,
    },
    {
      path: '/admin',
      element: <AdminLayout />,
      errorElement: <ErrorPage />,
      loader: adminLoader,
      children: [
        { index: true, element: <AdminHomePage /> },
        {
          path: 'user',
          children: [
            { index: true, element: <AdminUserPage /> },
            { path: 'import', element: <AdminUserImportPage /> },
            { path: 'new', element: <AdminUserEditPage /> },
            { path: ':id/edit', element: <AdminUserEditPage /> },
          ],
        },
        {
          path: 'announcement',
          children: [
            { index: true, element: <AdminAnnouncementPage /> },
            { path: 'new', element: <AdminAnnouncementEditPage /> },
            { path: ':id/edit', element: <AdminAnnouncementEditPage /> },
          ],
        },
        {
          path: 'problem',
          children: [
            { index: true, element: <AdminProblemPage /> },
            { path: 'new', element: <AdminProblemEditPage /> },
            { path: ':id/edit', element: <AdminProblemEditPage /> },
          ],
        },
        {
          path: 'contest',
          children: [
            { index: true, element: <AdminContestPage /> },
            { path: 'new', element: <AdminContestEditPage /> },
            { path: ':id/edit', element: <AdminContestEditPage /> },
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
            { index: true, element: <AdminLanguagePage /> },
            { path: 'new', element: <AdminLanguageEditPage /> },
            { path: ':id/edit', element: <AdminLanguageEditPage /> },
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
            { index: true, element: <AdminJudgeClientTokenPage /> },
            { path: 'new', element: <AdminJudgeClientTokenEditPage /> },
            { path: ':id/edit', element: <AdminJudgeClientTokenEditPage /> },
          ],
        },
      ],
    },
    {
      path: '/',
      element: <MainLayout />,
      errorElement: <ErrorPage />,
      loader: rootLoader,
      children: [
        { index: true, element: <HomePage /> },
        { path: 'about', element: <div>About</div> },
        {
          path: 'p',
          children: [
            { index: true, element: <ProblemListPage /> },
            { path: ':alias', element: <ProblemPage /> },
          ],
        },
        { path: 'a/:id', element: <AnnouncementPage /> },
        { path: 'u/:username', element: <UserProfilePage /> },
        {
          path: 'r',
          children: [
            { index: true, element: <RecordListPage /> },
            { path: ':id', element: <RecordPage /> },
          ],
        },
        {
          path: 'c',
          children: [
            { index: true, element: <ContestListPage /> },
          ],
        },
        {
          path: 'article',
          children: [
            { index: true, element: <ArticleListPage /> },
            { path: 'new', element: <ArticleEditPage /> },
            {
              path: ':id',
              children: [
                { index: true, element: <ArticleViewPage /> },
                { path: 'edit', element: <ArticleEditPage /> },
              ],
            },
            { path: ':id', element: <ArticleViewPage /> },
          ],
        },
      ],
    },
    {
      path: '/c/:id',
      element: <ContestLayout />,
      errorElement: <ErrorPage />,
      loader: rootLoader,
      children: [
        { index: true, element: <ContestHome /> },
        {
          path: 'p',
          children: [
            { index: true, element: <ContestProblemList /> },
            { path: ':alias', element: <ContestProblem /> },
          ],
        },
        {
          path: 'clarification',
          children: [
            { index: true, element: <ContestClarificationListPage /> },
            { path: 'new', element: <ContestNewClarificationPage /> },
            { path: ':clarificationId', element: <ContestClarificationDetailPage /> },
          ],
        },
        {
          path: 'submission',
          children: [
            { index: true, element: <ContestSubmissionListPage /> },
          ],
        },
        {
          path: 'standing',
          element: <ContestStandingPage />,
        },
      ],
    },
    {
      path: '*',
      element: <NotFoundPage />,
    },
  ],
)
