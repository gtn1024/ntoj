export const PERM = {
  // general
  PERM_VIEW: 1n << 0n,
  PERM_EDIT_SYSTEM: 1n << 1n,
  PERM_SET_PERM: 1n << 2n,
  PERM_USER_PROFILE: 1n << 3n,
  PERM_REGISTER_USER: 1n << 4n,
  PERM_JUDGE: 1n << 5n,
  PERM_EDIT_ANNOUNCEMENT: 1n << 6n,

  // problem
  PERM_PROBLEM_CREATE: 1n << 10n,
  PERM_EDIT_ALL_PROBLEMS: 1n << 11n,
  PERM_EDIT_OWN_PROBLEMS: 1n << 12n,
  PERM_SUBMIT_PROBLEM: 1n << 13n,
  PERM_VIEW_PROBLEMS: 1n << 14n,
  PERM_VIEW_HIDDEN_PROBLEMS: 1n << 15n,

  // record
  PERM_REJUDGE_RECORD: 1n << 20n,
  PERM_REJUDGE_PROBLEM: 1n << 21n,

  // article
  PERM_CREATE_ARTICLE: 1n << 30n,
  PERM_EDIT_ALL_ARTICLES: 1n << 31n,
  PERM_EDIT_OWN_ARTICLES: 1n << 32n,
  PERM_VIEW_ARTICLE: 1n << 33n,

  // contest
  PERM_CREATE_CONTEST: 1n << 40n,
  PERM_EDIT_ALL_CONTESTS: 1n << 41n,
  PERM_EDIT_OWN_CONTESTS: 1n << 42n,
  PERM_ATTEND_CONTEST: 1n << 43n,

  // homework
  PERM_CREATE_HOMEWORK: 1n << 50n,
  PERM_EDIT_HOMEWORK: 1n << 51n,
  PERM_EDIT_OWN_HOMEWORK: 1n << 52n,

  // group
  PERM_CREATE_GROUP: 1n << 60n,
  PERM_EDIT_GROUP: 1n << 61n,
  PERM_EDIT_OWN_GROUP: 1n << 62n,
}

interface Permission {
  family: string
  value: bigint
  desc: string
}

export const Perm = (family: string, value: bigint, desc: string) => ({ family, value, desc } as Permission)

export const PERMS = [
  Perm('general', PERM.PERM_VIEW, 'View system'),
  Perm('general', PERM.PERM_EDIT_SYSTEM, 'Edit system settings'),
  Perm('general', PERM.PERM_SET_PERM, 'Set user role'),
  Perm('general', PERM.PERM_USER_PROFILE, 'User profile'),
  Perm('general', PERM.PERM_REGISTER_USER, 'Register user'),
  Perm('general', PERM.PERM_JUDGE, 'Is user able to sign in as judger'),
  Perm('general', PERM.PERM_EDIT_ANNOUNCEMENT, 'Edit announcement'),

  Perm('problem', PERM.PERM_PROBLEM_CREATE, 'Create problem'),
  Perm('problem', PERM.PERM_EDIT_ALL_PROBLEMS, 'Edit all problems'),
  Perm('problem', PERM.PERM_EDIT_OWN_PROBLEMS, 'Edit own problems'),
  Perm('problem', PERM.PERM_SUBMIT_PROBLEM, 'Submit problem'),
  Perm('problem', PERM.PERM_VIEW_PROBLEMS, 'View problems'),
  Perm('problem', PERM.PERM_VIEW_HIDDEN_PROBLEMS, 'View hidden problems'),

  Perm('record', PERM.PERM_REJUDGE_RECORD, 'Rejudge record'),
  Perm('record', PERM.PERM_REJUDGE_PROBLEM, 'Rejudge problem'),

  Perm('article', PERM.PERM_CREATE_ARTICLE, 'Create article'),
  Perm('article', PERM.PERM_EDIT_ALL_ARTICLES, 'Edit all articles'),
  Perm('article', PERM.PERM_EDIT_OWN_ARTICLES, 'Edit own articles'),
  Perm('article', PERM.PERM_VIEW_ARTICLE, 'View article'),

  Perm('contest', PERM.PERM_CREATE_CONTEST, 'Create contest'),
  Perm('contest', PERM.PERM_EDIT_ALL_CONTESTS, 'Edit all contests'),
  Perm('contest', PERM.PERM_EDIT_OWN_CONTESTS, 'Edit own contests'),
  Perm('contest', PERM.PERM_ATTEND_CONTEST, 'Attend contest'),

  Perm('homework', PERM.PERM_CREATE_HOMEWORK, 'Create homework'),
  Perm('homework', PERM.PERM_EDIT_HOMEWORK, 'Edit homework'),
  Perm('homework', PERM.PERM_EDIT_OWN_HOMEWORK, 'Edit own homework'),

  Perm('group', PERM.PERM_CREATE_GROUP, 'Create group'),
  Perm('group', PERM.PERM_EDIT_GROUP, 'Edit group'),
  Perm('group', PERM.PERM_EDIT_OWN_GROUP, 'Edit own group'),
]

export const PERMS_BY_FAMILY: Record<string, Permission[]> = {}
for (const perm of PERMS) {
  if (!PERMS_BY_FAMILY[perm.family]) {
    PERMS_BY_FAMILY[perm.family] = []
  }
  PERMS_BY_FAMILY[perm.family].push(perm)
}

export function checkPermission(perm: bigint, target: bigint): boolean {
  if (perm === -1n) {
    return true
  }
  return (perm & target) === target
}
